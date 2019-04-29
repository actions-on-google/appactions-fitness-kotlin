# App Actions Fitness Basic Sample

Using App Actions, your app can be triggered by the Google Assistant when using an Android device.
To support App Actions, your app must implement Deep Links to your app content, allowing the
Android system to invoke a specific Activity when an action is performed.

App Actions are triggered by Built-In Intents (BIIs) handled by the Google Assistant, that will enable
your app to receive user's input without creating a conversational Action, or having to deal with
NLP by yourself. The BIIs are grouped by features vertical, and the verticals currently available
are:

* Ride sharing
* Fitness
* Finance
* Food ordering

The BIIs should be mapped into a file named actions.xml, where the Deep Link and BII information
must be configured. Check-out our documentation for a full developer's guide for App Actions. 

## Sample requirements

* API Level 21 - Android 5.0 Lollipop 
* Android JetPack libraries
* Google Assistant available on the test / target device

## Executing the sample

## How to test

## License

Copyright 2019 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.