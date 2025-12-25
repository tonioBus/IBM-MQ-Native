package com.aquila.mq.jna;

import com.aquila.mq.jna.lib.IBMMQJNA;
import com.aquila.mq.jna.lib.MQCD;
import com.aquila.mq.jna.lib.MQCNO;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.ibm.mq.constants.CMQC.*;
import static com.ibm.mq.constants.CMQXC.*;

@Slf4j
public class MainIBMMQJNA {

    public static void main(String[] args) {
        log.info("Starting IBM MQ JNA Test");
        // Configuration de la connexion
        String queueManagerName = "QM1";
        String channelName = "DEV.APP.SVRCONN";  // Canal par défaut dans l'image Docker IBM MQ
        String connectionName = "localhost(1414)";  // Host:port

        // Préparer le nom du Queue Manager (48 bytes, rempli d'espaces)
        byte[] qmgrName = new byte[IBMMQJNA.MQ_Q_MGR_NAME_LENGTH];
        Arrays.fill(qmgrName, (byte) ' ');
        byte[] qmgrBytes = queueManagerName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(qmgrBytes, 0, qmgrName, 0, Math.min(qmgrBytes.length, qmgrName.length));

        // Créer et configurer la structure MQCD (Channel Definition)
        MQCD mqcd = new MQCD();
        mqcd.Version = MQCD_VERSION_10;
        mqcd.setChannelName(channelName);
        mqcd.setConnectionName(connectionName);
        mqcd.setUser("app");
        mqcd.setPassword("passw0rd");
        mqcd.ChannelType = MQCHT_CLNTCONN;
        mqcd.TransportType = MQXPT_TCP;

        // Note: Le mot de passe ne se met pas dans MQCD mais dans MQCSP
        // Pour DEV.ADMIN.SVRCONN, l'authentification peut être requise

        // Créer et configurer la structure MQCNO (Connection Options)
        MQCNO mqcno = new MQCNO();
        mqcno.setClientConnection(mqcd);

        // Variables de sortie
        IntByReference hConn = new IntByReference(MQHC_UNUSABLE_HCONN);
        IntByReference compCode = new IntByReference();
        IntByReference reason = new IntByReference();

        // Connexion au Queue Manager via TCP/IP
        log.info("========================================");
        log.info("Connexion au Queue Manager via TCP/IP");
        log.info("  Queue Manager: {}", queueManagerName);
        log.info("  Canal: {}", channelName);
        log.info("  Connexion: {}", connectionName);
        log.info("========================================");

        try {
            IBMMQJNA.INSTANCE.MQCONNX(new String(qmgrName), mqcno, hConn, compCode, reason);

            // Vérifier le résultat
            if (compCode.getValue() == MQCC_FAILED) {
                log.error("ERREUR: MQCONNX a échoué");
                log.error("  Completion Code: {}", compCode.getValue());
                log.error("  Reason Code: {}", reason.getValue());
                printReasonCode(reason.getValue());
                System.exit(1);
            }

            log.info("Connecté avec succès!");
            log.info("  Handle de connexion: {}", hConn.getValue());
            log.info("  Completion Code: {}", compCode.getValue());

            // Déconnexion propre
            log.info("Déconnexion...");
            IBMMQJNA.INSTANCE.MQDISC(hConn, compCode, reason);

            if (compCode.getValue() == MQCC_OK) {
                log.info("Déconnecté avec succès!");
            } else {
                log.warn("Avertissement lors de la déconnexion");
                log.warn("  Completion Code: {}", compCode.getValue());
                log.warn("  Reason Code: {}", reason.getValue());
            }

        } catch (UnsatisfiedLinkError e) {
            log.error("========================================");
            log.error("ERREUR: Bibliothèque IBM MQ non trouvée!");
            log.error("========================================");
            log.error("La bibliothèque native IBM MQ (mqm.dll ou libmqm.so) n'est pas disponible.");
            log.error("");
            log.error("Pour résoudre ce problème:");
            log.error("1. Installez le client IBM MQ:");
            log.error("   Windows: https://www.ibm.com/support/pages/downloading-ibm-mq-clients");
            log.error("   Linux: sudo apt-get install ibmmq-client (ou via yum/dnf)");
            log.error("");
            log.error("2. Ajoutez le répertoire contenant mqm.dll/libmqm.so au PATH système");
            log.error("   Windows: C:\\Program Files\\IBM\\MQ\\bin (ou bin64)");
            log.error("   Linux: /opt/mqm/lib64");
            log.error("");
            log.error("Erreur détaillée: ", e);
            System.exit(1);
        } catch (Exception e) {
            log.error("Erreur inattendue: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Affiche le message d'erreur correspondant au code raison
     */
    private static void printReasonCode(int reason) {
        System.err.println("");
        System.err.println("========================================");
        System.err.println("Diagnostic de l'erreur:");
        System.err.println("========================================");
        switch (reason) {
            case MQRC_ENVIRONMENT_ERROR:
                System.err.println("  MQRC_ENVIRONMENT_ERROR (2012)");
                System.err.println("");
                System.err.println("  → Erreur d'environnement IBM MQ");
                System.err.println("  → Les structures MQCD/MQCNO sont probablement mal configurées");
                System.err.println("");
                System.err.println("  Causes possibles:");
                System.err.println("    1. Problème de taille/padding dans les structures JNA");
                System.err.println("    2. Version de structure incompatible");
                System.err.println("    3. Architecture 32-bit/64-bit mismatch");
                System.err.println("    4. Champs de structure non initialisés correctement");
                System.err.println("");
                System.err.println("  Solutions à essayer:");
                System.err.println("    • Simplifier MQCD en utilisant VERSION_1 au lieu de VERSION_11");
                System.err.println("    • Vérifier que JVM et client MQ ont la même architecture (64-bit)");
                System.err.println("    • Essayer avec le canal DEV.APP.SVRCONN au lieu de DEV.ADMIN.SVRCONN");
                System.err.println("    • Vérifier les logs du serveur: podman logs QM1");
                System.err.println("    • Activer le mode debug JNA: -Djna.dump_memory=true");
                break;
            case MQRC_NOT_AUTHORIZED:
                System.err.println("  MQRC_NOT_AUTHORIZED (2035)");
                System.err.println("");
                System.err.println("  → Problème d'authentification");
                System.err.println("  → Le canal DEV.ADMIN.SVRCONN nécessite probablement une authentification");
                System.err.println("");
                System.err.println("  Solutions:");
                System.err.println("    • Utiliser DEV.APP.SVRCONN (pas d'auth requise)");
                System.err.println("    • OU implémenter MQCSP pour fournir user/password");
                break;
            case MQRC_Q_MGR_NOT_AVAILABLE:
                System.err.println("  MQRC_Q_MGR_NOT_AVAILABLE (2059)");
                System.err.println("");
                System.err.println("  → Le Queue Manager n'est pas accessible");
                System.err.println("");
                System.err.println("  Solutions:");
                System.err.println("    • Vérifier que QM1 est bien le nom du Queue Manager");
                System.err.println("    • Vérifier le nom du canal: DEV.APP.SVRCONN ou DEV.ADMIN.SVRCONN");
                System.err.println("    • Vérifier la connexion: 172.20.26.188(1414)");
                System.err.println("    • Tester avec telnet: telnet 172.20.26.188 1414");
                break;
            case MQRC_HOST_NOT_AVAILABLE:
                System.err.println("  MQRC_HOST_NOT_AVAILABLE (2538)");
                System.err.println("");
                System.err.println("  → Le serveur n'est pas accessible sur le réseau");
                System.err.println("");
                System.err.println("  Solutions:");
                System.err.println("    • Vérifier que le conteneur est démarré: podman ps | grep QM1");
                System.err.println("    • Vérifier l'IP: 172.20.26.188");
                System.err.println("    • Vérifier le port: 1414");
                System.err.println("    • Tester: telnet 172.20.26.188 1414");
                break;
            default:
                System.err.println("  Code raison: " + reason);
                System.err.println("");
                System.err.println("  → Consultez la documentation IBM MQ:");
                System.err.println("    https://www.ibm.com/docs/en/ibm-mq/9.3?topic=codes-mqrc-" + reason);
        }
        System.err.println("========================================");
        System.err.println("");
    }
}
