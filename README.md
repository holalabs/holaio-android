# Android library for HolaIO
This API can be used on Android > 2.2

To use it, you must [download the library](https://github.com/downloads/holalabs/holaio-android/HolaIO.zip),
put it in your workspace, and import it from Eclipse. After that, you have to go to your project's properties, go to the Android menu and add HolaIO as a library.

You can aslo check out the [demo app](https://github.com/holalabs/holaio-android/tree/master/Example)
and download it's [APK](https://github.com/holalabs/holaio-android/HolaIODemo.apk/qr_code)

## Object HolaIO(String APIKey)

Creates an instance of HolaIO with the specified API key.

Usage:

``` java
HolaIO io = new HolaIO("yourapikey");
```

## Function HolaIO.get(String url, String selector, Boolean inner, new AsyncResponseHandler() {})

Get the content specified in the following (obligatory) parameters:

  - URL: A valid URL without the protocol scheme, because holaIO currently only works with HTTP so it’ll add the prefix by default. Example: `holalabs.com`
  - Selector: A valid CSS3 selector. If you want to get more than a selector at a time, strip them by commas. Example: `a, .primary.content`
  - Inner or outer: Specify if you want to extract the innerHTML content or the whole content of your selection (outerHTML). Possible values: true for inner or false for outer
  - AsyncResponseHandler(): Pass a new AsyncResponseHandler() and override onSuccess(JsonObject content), this is where you do what you need to do with your content. You can also override onFinish() to run code after the thread that gets the content has ended. This can be used to update the UI for example.

Usage:

``` java
HolaIO io = new HolaIO("yourapikey");
io.get("google.com", "a span", "inner", new AsyncResponseHandler() {
@Override
public void onSuccess(JSONObject content) {
// Have fun!
}
@Override
public void onFinish() {
// Whatever you need to do after onSuccess like updating the UI.
}
});
```
