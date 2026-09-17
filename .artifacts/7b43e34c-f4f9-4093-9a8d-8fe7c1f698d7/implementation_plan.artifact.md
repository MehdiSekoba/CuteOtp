# Fix Fbns AIDL RemoteException

The application is encountering a `RemoteException: AIDLService is not bound` from the `FbnsAIDLClientManager`. This error is typically associated with the Facebook Notification Service (FBNS) and occurs when the client library attempts to communicate with a Facebook-related service that is not visible or not bound.

On Android 11 (API level 30) and higher, package visibility restrictions prevent apps from seeing other installed apps unless they are explicitly declared in the manifest. This can cause AIDL binding to fail if the target service belongs to another package (like Facebook or Instagram).

## Proposed Changes

### [app](file:///F:/program/AndroidStudioProjects/sample/app)

#### [MODIFY] [AndroidManifest.xml](file:///F:/program/AndroidStudioProjects/sample/app/src/main/AndroidManifest.xml)

Add a `<queries>` section to declare visibility for common Facebook-related packages that provide FBNS services. This ensures that the library can successfully bind to the external AIDL service.

## Verification Plan

### Manual Verification
1. Deploy the application to a device or emulator running Android 11+.
2. Monitor Logcat for the `FbnsAIDLClientManager` tag.
3. Verify that the `RemoteException: AIDLService is not bound` no longer occurs during application startup or when the library attempts to connect.
