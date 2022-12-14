# *:warning: Warning: Go to [App Actions fitness sample](https://github.com/android/app-actions-samples/tree/main/fitness-biis) or [App Actions Widget sample](https://github.com/android/app-actions-samples/tree/main/widget) for maintained sample.

*:warning: Warning: This [App Actions fitness sample](https://github.com/android/app-actions-samples/tree/main/fitness-biis) and [App Actions Widget sample](https://github.com/android/app-actions-samples/tree/main/widget) has migrated to [App-Action directory](https://github.com/android/app-actions-samples) in Android Github. This version will no longer be maintained.*

This is a sample Fitness application that allows displaying workout information as well as starting
and stopping a workout. By implementing [App Actions](ttps://developer.android.com/guide/app-actions/overview),
we allow the user to call upon our application to fulfill supported
[built-in intents (BIIs)](https://developer.android.com/reference/app-actions/built-in-intents) in the
fitness category.

Specifically, this sample supports the following BIIs:
* `actions.intent.START_EXERCISE`: Open the sample app and start an exercise session.
* `actions.intent.STOP_EXERCISE`: Open the sample app and stops the current exercise session.
* `actions.intent.GET_EXERCISE_OBSERVATION`: Display a
  [widget](https://developers.google.com/assistant/app/widgets) inside the Assistant with information on a
  particular exercise statistic.

![alt-text](media/fit-actions-demo.gif "App Actions Demo")

## How to use this sample

Clone or download the project to your preferred location. Then, import and modify the project with the following steps:

1. In Android Studio, select **Open an existing Android Studio project** from the initial screen, or go to **File > Open**.
2. Change the `applicationId` in [app/build.gradle](app/build.gradle) to the `applicationId` of one of your draft or published apps in the Google Play Console.

    ```groovy
    android {
        defaultConfig {
            // This ID uniquely identifies your app on the device and in Google Play
            applicationId "PUT_YOUR_APPLICATION_ID_HERE"
        }
    }
    ```

3. Change the three (3) `android:targetPackage` in [app/src/main/res/xml/shortcuts.xml](app/src/main/res/xml/shortcuts.xml) to the `applicationId` in your [app/build.gradle](app/build.gradle).

    ```xml
    <capability android:name="actions.intent.START_EXERCISE">
        <intent
            android:targetPackage="PUT_YOUR_APPLICATION_ID_HERE"
            >
        </intent>
    </capability>
    ```

    ```xml
    <capability android:name="actions.intent.STOP_EXERCISE">
        <intent
            android:targetPackage="PUT_YOUR_APPLICATION_ID_HERE"
            >
        </intent>
    </capability>
    ```
   
    ```xml
    <capability android:name="actions.intent.GET_EXERCISE_OBSERVATION">
        <intent
            android:targetPackage="PUT_YOUR_APPLICATION_ID_HERE"
            >
        </intent>
    </capability>
    ```

4. In Android Studio, find the root directory of the sample.
5. Select the `build.gradle` file.
6. Follow the instructions presented by the IDE.
7. Install [Google Assistant plugin for Android Studio](https://developers.google.com/assistant/app/test-tool)


Then, you can try the App Actions by following these steps:

1. Build and run the sample on your physical test device (**Run "app"**).
2. Open the App Actions test tool (**Tools > Google Assistant > App Actions Test Tool**).
3. Define an invocation name to use for invoking the App Actions (like "my test app"). This name is only for testing purposes, so it can be different from what you want to deploy to production later.
4. Click **Create Preview**. Once your preview is created, the test tool window updates to display information about BIIs found in your `shortcuts.xml` file.

After you create a preview, you can then try using voice or written commands directly with Assistant on your test device.

If you run into any issues, check out the [troubleshooting guide](https://developers.google.com/assistant/app/troubleshoot) in our developer documentation.

## Contribution guidelines

If you want to contribute to this project, be sure to review the
[contribution guidelines](CONTRIBUTING.md).

We use [GitHub issues](https://github.com/actions-on-google/appactions-fitness-kotlin/issues) for
tracking requests and bugs, please get support by posting your technical questions to
[Stack Overflow](https://stackoverflow.com/questions/tagged/app-actions).

Report [general issues with App Actions features](https://issuetracker.google.com/issues/new?component=617864&template=1257475)
or [make suggestions for additional built-in intents](https://issuetracker.google.com/issues/new?component=617864&template=1261453)
through our public issue tracker.

## References

* [App Actions Overview](https://developers.google.com/assistant/app/overview)
* [Built-in Intents reference](https://developers.google.com/assistant/app/reference/built-in-intents/bii-index)
* [App Actions Test Tool](https://developers.google.com/assistant/app/test-tool)
* [Codelab](https://developers.google.com/assistant/app/codelabs)
* [Other samples](https://developers.google.com/assistant/app/samples)

## License
```
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
```