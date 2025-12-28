# IBM-MQ-Native

A Java project demonstrating native IBM MQ integration with GraalVM native image compilation. This project compares performance between standard IBM MQ client connections and JNA-based native connections.

## Overview

This project showcases two different approaches to connecting to IBM MQ (Message Queue):

1. **Standard Approach**: Using the official IBM MQ Jakarta client library
2. **JNA-based Native Approach**: Direct calls to IBM MQ native libraries (C API) via JNA

Both approaches are compiled to native executables using GraalVM, demonstrating significant performance improvements in startup time and message throughput.

## Key Features

- **Dual Connection Methods**: Compare Java-based vs native-based IBM MQ connections
- **GraalVM Native Image**: Ahead-of-time compilation to native executables
- **Performance Benchmarking**: Measure and compare operation timings
- **JNA Integration**: Low-level Java-to-C interoperability with IBM MQ libraries
- **Cross-platform Support**: Works with Windows (mqm.dll) and Linux (libmqm.so)

## Performance Results

Based on 1000 message operations:

| Approach | Open Delay | Put Delay | Get Delay | Total Time |
|----------|-----------|-----------|-----------|------------|
| Java Full | 32ms | 365ms | 355ms | 753ms |
| Java Native (GraalVM) | 5ms | 270ms | 293ms | 568ms |
| **Improvement** | **6.4x faster** | **1.35x faster** | **1.21x faster** | **1.33x faster** |

**Key Findings**:
- Queue manager initialization overhead reduced by 84% (32ms → 5ms)
- Overall throughput improved by approximately 33%
- Native image particularly benefits connection setup operations

## Project Structure

```
IBM-MQ-Native/
├── src/main/java/com/aquila/mq/
│   ├── jna/                        # JNA-based native approach
│   │   ├── MainIBMMQJNA.java       # Main class for JNA implementation
│   │   └── lib/
│   │       ├── IBMMQJNA.java       # JNA interface to native IBM MQ libraries
│   │       ├── MQCD.java           # Channel definition structure
│   │       └── MQCNO.java          # Connection options structure
│   └── normal/                     # Standard approach
│       └── NormalMQ.java           # Standard IBM MQ client implementation
├── src/test/java/                  # Unit tests
├── src/main/resources/META-INF/native-image/  # GraalVM configuration
│   ├── jni-config.json
│   ├── reflect-config.json
│   ├── resource-config.json
│   ├── proxy-config.json
│   ├── serialization-config.json
│   └── predefined-classes-config.json
├── pom.xml                         # Maven build configuration
└── doc/score.txt                   # Performance benchmark results
```

## Technologies Used

- **Java 17**: Base language
- **GraalVM Native Image**: AOT compilation to native executables
- **JNA 5.18.1**: Java Native Access for C library integration
- **IBM MQ Jakarta Client 9.4.4.1**: Official IBM MQ client library
- **Maven**: Build tool with native-maven-plugin
- **SLF4J + Logback**: Logging framework
- **JUnit Jupiter**: Testing framework

## Prerequisites

1. **Java 17** or later
2. **GraalVM** with native-image installed
3. **IBM MQ Server** running locally or accessible remotely
4. **Maven** 3.6+

### IBM MQ Configuration Required

- Queue Manager: `QM1`
- Queue: `DEV.QUEUE.1`
- Channel: `DEV.APP.SVRCONN` (standard) or `DEV.ADMIN.SVRCONN` (JNA)
- Host: `localhost:1414`
- User: `app` with appropriate permissions
- Listener: `SYSTEM.LISTENER.TCP.1` on port 1414

## Building the Project

### Standard JAR Build

```bash
mvn clean package
```

### Native Image Build

Build both native executables:

```bash
mvn clean package -Pnative
```

This creates two native executables:
- `MainIBMMQJNA` - JNA-based native implementation
- `NormalMQ` - Standard IBM MQ client native implementation

## Running the Application

### Using JAR (Standard Approach)

```bash
java -jar target/IBMMQNative-1.0-SNAPSHOT.jar
```

### Using Native Executables

```bash
# JNA-based approach
./target/MainIBMMQJNA

# Standard approach
./target/NormalMQ
```

## Implementation Details

### Standard Approach (`NormalMQ.java`)

Uses the official IBM MQ Jakarta client library with high-level Java APIs:
- `MQQueueManager` - Connection management
- `MQQueue` - Queue operations
- `MQMessage` - Message handling

### JNA-based Approach (`MainIBMMQJNA.java`)

Directly interfaces with native IBM MQ C libraries:
- Custom JNA structure definitions (MQCD, MQCNO)
- Direct calls to native functions (MQCONNX, MQOPEN, MQPUT, MQGET, MQCLOSE, MQDISC)
- Manual memory management and structure initialization
- Platform-specific library loading (libmqm.so on Linux, mqm.dll on Windows)

### GraalVM Native Image Configuration

The project includes comprehensive GraalVM metadata:
- **JNI Configuration**: Native interop with IBM MQ and JNA libraries
- **Reflection Configuration**: Runtime reflection for IBM MQ client components
- **Resource Configuration**: Bundle inclusion (MQ metadata, JNA dispatchers, configs)
- **Proxy Configuration**: Dynamic proxy interfaces for JNA binding

## Error Handling

The application includes comprehensive error diagnostics for common IBM MQ issues:

- **MQRC_ENVIRONMENT_ERROR (2012)**: Structure/alignment issues
- **MQRC_NOT_AUTHORIZED (2035)**: Authentication problems
- **MQRC_Q_MGR_NOT_AVAILABLE (2059)**: Queue manager not accessible
- **MQRC_HOST_NOT_AVAILABLE (2538)**: Network connectivity issues

Each error includes suggested solutions and diagnostic steps.

## Use Cases

This project is useful for:

1. **Performance Optimization**: Understanding GraalVM native image benefits for IBM MQ applications
2. **Native Integration**: Learning how to use JNA for direct C library calls
3. **Enterprise Messaging**: Benchmarking different IBM MQ connection approaches
4. **Cloud Native Applications**: Building fast-starting, low-memory IBM MQ clients
5. **Educational Purposes**: Demonstrating JNA usage and GraalVM configuration

## License

This project is provided as-is for educational and demonstration purposes.

## Contributing

Feel free to submit issues or pull requests for improvements.

## Acknowledgments

- IBM MQ team for the robust messaging platform
- GraalVM team for the native image technology
- JNA project for enabling Java-to-C interoperability
