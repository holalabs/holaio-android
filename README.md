# Android library for HolaIO
This API can be used on Android > 2.2

To use it, you must [download the library](https://github.com/downloads/holalabs/holaio-android/HolaIO.zip),
put it in your workspace, and import it from Eclipse. After that, you have to go to your project's properties, go to the Android menu and add HolaIO as a library.

You can also check out the [demo app](https://github.com/holalabs/holaio-android/tree/master/Example)
and download it's [APK](https://github.com/holalabs/holaio-android/HolaIODemo.apk/qr_code)

## Object HolaIO(String APIKey)

Creates an instance of HolaIO with the specified API key.

Usage:

``` java
HolaIO io = new HolaIO("yourapikey");
```

## Function HolaIO.get(String url, String selector, Boolean inner, Boolean cache, new AsyncResponseHandler() {})

Get the content specified in the following (obligatory) parameters:

  - URL: A valid URL without the protocol scheme, because holaIO currently only works with HTTP so it’ll add the prefix by default. Example: `holalabs.com`
  - Selector: A valid CSS3 selector. If you want to get more than a selector at a time, strip them by commas. Example: `a, .primary.content`
  - Inner or outer: Specify if you want to extract the innerHTML content or the whole content of your selection (outerHTML). Possible values: true for inner or false for outer
  - Cache: You have the possibility of caching the content received by your request. In that case, when doing that request a second time while the app stays opened, it will get the content cached instead of doing the request again. Possible values: "true" if you would like to cache the request and "false" if not.
  - AsyncResponseHandler(): Pass a new AsyncResponseHandler() and override onSuccess(JsonObject content), this is where you do what you need to do with your content. You can also override onFinish() to run code after the thread that gets the content has ended. This can be used to update the UI for example. There's also onError(Throwable error, String response) where you can handle any errors that might occur during the request.

Usage:

``` java
HolaIO io = new HolaIO("yourapikey");
io.get("google.com", "a span", true, true, new AsyncResponseHandler() {
	@Override
	public void onSuccess(JSONObject content) {
		// Have fun!
	}
	@Overrid
	public void onError(Throwable error, String response) {
		// Handle the error
	}
	@Override
	public void onFinish() {
		// Whatever you need to do after onSuccess like updating the UI.
	}
});
```
