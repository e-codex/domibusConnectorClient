# Domibus Web Connector 4.0 Client


## Module description

The project contains multiple maven modules. The following list gives a short introduction about these modules.


| Module                                                     | Description                                                                             |
|------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| domibusConnectorClientApi                                  | Contains the entity classes and interfaces wich are implemented by the link modules     |
| domibusConnectorClientLibrary                              | Contains configuration and basic implementation of the client api interfaces            |
| domibusConnectorClientWsLink                               | Contains classes, services and configuration to connect to the web connector over web services (via cxf) this module can be replaced to use alternative message transport. The counterpart on connector side is the domibusConnectorBackendLink module
| domibusConnectorClient35Library                            | This module contains the old connector 3.5 interfaces and is compatibel to the old connector framework. It has been designed to make it possibly to just replace the connector framework with this library |
| domibusConnectorClientRunnable                             | This module contains a gui mainly for testing purposes, it uses the Client35Library for talking with the web connector 4.0 |
| domibusConnectorClientConfiguration                        | This module provides a configuration helper for creating a client configuration |
| domibusConnectorClientDistribution                         | This module contains multiple assembly scripts to package the artifacts for delivery |



