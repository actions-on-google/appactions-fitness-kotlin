# App Actions Fitness Basic Sample

This is a sample Fitness application that allows displaying workout information as well as starting
and stopping a workout. By implementing [App Actions](https://developers.google.com/actions/app/ ),
we allow the user to call upon our application to fulfill supported actions from our Fitness
vertical Assistant
[built-in intents (BIIs)](https://developers.google.com/actions/discovery/built-in-intents).

Specifically, this sample supports the following BIIs:
* `START_EXERCISE` - opens the sample app and starts an exercise session
* `STOP_EXERCISE` - opens the sample app and stops the current exercise session.
* `GET_EXERCISE_OBSERVATION` - displays a [Slice](https://developer.android.com/guide/slices) inside
the Assistant with information on a particular exercise statistic

## App Actions overview

With [App Actions](https://developers.google.com/actions/app/ ), your app can be triggered by users
through the Google Assistant on Android devices. To support App Actions, your app must implement
[Android deep links](https://developer.android.com/training/app-links/deep-linking) to your app
content, allowing the Android system to invoke a specific Activity when the App Action is triggered.

Users invoke App Actions through phrases associated with
[built-in intents (BIIs)](https://developers.google.com/actions/discovery/built-in-intents). The
Assistant handles these BIIs so that your app receives the user's input without you needing to
create a conversational Action, or having to deal with natural language processing by yourself.

You map the BIIs that are relevant to your app and their parameters to your Android deep links
within a file named `actions.xml`. For more details, check out our developer documentation for App
Actions.

## Sample requirements

* [API Level 21 - Android 5.0 Lollipop](https://developer.android.com/about/versions/android-5.0)
* [Android JetPack support](https://developer.android.com/jetpack/)
* [Google Assistant](https://assistant.google.com/) available on the test / target device or a
emulator configured with Google Play. Check the
[Run apps on the emulator](https://developer.android.com/studio/run/emulator) guide for details.
* App Actions Test Tool Android Studio plugin. The installation instructions are listed below.

## Importing this sample

Check out or download the project to your preferred location. You can import the project using the
Android Studio with the following steps:

1. Select "**Open an existing Android Studio project**" from the initial screen, or **File --> Open**
2. Find the directory where you saved the sample.
3. Select the `build.gradle` file
4. Follow the instructions presented by the IDE.

## How to test

To develop and test your App Actions, you will need to install the App Actions plugin. The plugin
will parse your `actions.xml` and fils in default values for the relevant BII parameters. You can
modify these parameter values to test that your App Actions provide the correct result.

*Attention*: To test the Assistant integration, make sure that you've logged in to the test device
and to the Android Studio with the same Google account.

### Installing the App Actions plugin

1. If you are using Linux or Windows, click on **File → Settings**. For MacOS, click on
**Android Studio → Preferences**
2. Select the **Plugins** section
3. Search for "**App Actions Test Tool**"
4. **Install** and restart your IDE

### Running the sample

Using a compatible device or the emulator, click on '**Run app (Shift+F10)**', and selected the 
device you want to test. After the APK installation, you will be able to test using one of these
approaches:

* Start the Assistant and try saying: “Start my run using Fitness Action Sample”
* Open the App Actions Test Tool, select the desired BII, and click **Send**.

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
