#BellaVista

The BellVista client is an implementation of a Native, Multi-Platform Clinical Client that is EMR agnostic. It is part of a new open source Clinical Client Platform (CCP). This platform provides a framework and an implementation of a Native, Multi-Platform Clinical Client that is EMR agnostic. The client provides core clinical results reporting functionality and a framework for integrating external functionality like:

* QR code and Bluetooth Beacon based patient identification
* Real-time patient vitals monitoring and EKGs
* Video Conferencing and Collaboration
* Voice based ordering and clinical note taking

The goal is to evolve this client over time, providing implementations of the above specified framework components and supporting the integration of new technologies and clinical information sources (like FHIR, OpenMRS ) as they emerge. This will allow third parties and individual organizations to tailor the client to their needs or the needs of specific populations within their organization. This first version of the client provides the core clinical results reporting functionality. 

The client is built on a unique platform that makes it easy to support multiple operating systems with minimal effort. It also allows the UI to be managed and update from the server-side (similar to a web application except that this client 100% native). This allows you to provide roll-based clinical UI’s instead of the traditional monolithic one-size fits all approach.

Currently the client is runs natively on iOS, Android, and on any desktop environment supporting Java.

The client has a demo mode which allows you to tests out its UI and functionality without having to talk to any back end systems.

##Latest News
###17 Feb 2016
* Support for direct access to FHIR servers added, see the FHIR section in the [Working with the Client](Configuring) page
* Documented how to test the Bluetooth Beacon support, see the Bluetooth Beacons section in the [Working with the Client](Configuring) page
* Experimental Order Entry workflow added, log into the 'Local Demo' server, choose a patient and then click on the 'New Orders' button on the action bar
* iOS version available for testing for the next 60 days via Apple's beta testing program. Send an email to <bvbeta1@sparseware.com> to get a copy. This version includes the real-time vital signs (numerics and waveforms) functionality, demonstrating how integration would work in the real world.


##Screenshots & Design

Please see the [WiKi](https://github.com/sparseware/ccp-bellavista/wiki) for screenshots and design information

##Binaries
Some platform binaries are available in the release section of the repo.

* iOS - You can compile your own version from the repo or you can send an email to <bvbeta1@sparseware.com> and if there is currently a version available via Apple's TestFlight beta testing program, you will be added as a tester. You need iOS version 8 or later to participate in the beta testing program, version 7+ if you compile the application yourself. 
* Android - Available in the repo and can be downloaded directly to your Android device from [here](https://github.com/sparseware/ccp-bellavista/releases/download/v0.9.5/bellavista-android-0.9.5.apk). You need Android version 4.4.2 or later
* Desktop - Available in the repo can be downloaded directly to your Mac or PC device from [here](https://github.com/sparseware/ccp-bellavista/releases/download/v0.9.5/bellavista-desktop-0.9.5.jar). You need Java 1.8.0_72 (on you Mac or PC) or newer


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

While the documentation is under development (check it's status in the WiKi), the BellaVista-android/assets/hub directory contains the data used in demo mode and mirrors the REST API (sans supported optional parameters) used when accessing a web service. To learn more about the API you can look at the code in the [RaVe](https://github.com/sparseware/ccp-rave) project here on GitHub

##Discussions
For general discussions regarding the platform please join the [Clinical Client Platform (CCP) discussion group](http://groups.google.com/d/forum/clinical-client-platform)

## License
BellaVista is available under the Apache license. See the [LICENSE](LICENSE) file for more info.

##Acknowledgements
Most of the icons used in this project are from <a xmlns:cc='http://creativecommons.org/ns#' href='http://www.snipicons.com' target='_blank' property='cc:attributionName' rel='cc:attributionURL'>www.snipicons.com</a>

A few others are from <a xmlns:cc='http://creativecommons.org/ns#' href='http://onlinepharmacycheck.com/medico/' target='_blank' property='cc:attributionName' rel='cc:attributionURL'>onlinepharmacycheck.com</a>
