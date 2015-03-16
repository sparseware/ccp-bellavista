#BellaVista
BellaVista is part of the open source Clinical Client Platform (CCP). This platform provides a framework and an implementation of a Native, Multi-Platform Clinical Client that is EMR agnostic. The client provides core clinical results reporting functionality and a framework for integrating external functionality like:

* QR code and Bluetooth Beacon based patient identification
* Real-time patient vitals monitoring and EKGs
* Video Conferencing and Collaboration
* Voice based ordering and clinical note taking

The goal is to evolve this client over time, providing implementations of the above specified framework components and supporting the integration of new technologies and clinical information sources (like FHIR, OpenMRS ) as they emerge. This will allow third parties and individual organizations to tailor the client to their needs or the needs of specific populations within their organization. This first version of the client provides the core clinical results reporting functionality. 

The client is built on a unique platform that makes it easy to support multiple operating systems with minimal effort. It also allows the UI to be managed and update from the server-side (similar to a web application except that this client 100% native). This allows you to provide roll-based clinical UI’s instead of the traditional monolithic one-size fits all approach.

Currently the client is runs natively on iOS, Android, and on any desktop environment supporting Java.

The client has a demo mode which allows you to tests out its UI and functionality without having to talk to any back end systems.

##Development Requirements
* Eclipse 4.3 or newer
* appNativa SDK v1.2
* Apple Mac w/ Xcode 6.2 (to build the iOS version)
* Android Eclipse ADT plugin (to build the android version)

To actually have the client work with a real EMR system, a RESTful web service is needed that supports the REST API used by the client and can translate the API calls into the appropriate actions to be taken by the EMR. A proof of concept implementation for the freely available VA VistA EMR is available here on GitHub in the [RaVe](https://github.com/sparseware/ccp-rave) project.

##Getting Started
* Download and install the [appNativa SDK](http://www.appnativa.com/downloads)
* Clone this project to the projects directory of the SDK
* Select “Import” from the Eclipse file and then choose the top level BellaVista directory to import all the projects.

While the REST API documentation is pending (check it status in the WiKi), the BellaVista-android/assets/hub directory contains the data used in demo mode and mirrors the REST API (sans supported optional parameters) used when accessing a web service. To learn more about the API you can look at the code in the [RaVe](https://github.com/sparseware/ccp-rave) project here on GitHub

##Discussions
For general discussions regarding the platform please join the [Clinical Client Platform (CCP) discussion group](http://groups.google.com/d/forum/clinical-client-platform)

## License
BellaVista is available under the Apache license. See the LICENSE file for more info.

##Acknowledgements
Most of the icons used in this project are from <a xmlns:cc='http://creativecommons.org/ns#' href='http://www.snipicons.com' target='_blank' property='cc:attributionName' rel='cc:attributionURL'>www.snipicons.com</a>

A few others are from <a xmlns:cc='http://creativecommons.org/ns#' href='http://onlinepharmacycheck.com/medico/' target='_blank' property='cc:attributionName' rel='cc:attributionURL'>onlinepharmacycheck.com</a>
