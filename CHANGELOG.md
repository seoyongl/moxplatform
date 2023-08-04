# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

## 2023-08-04

### Changes

---

Packages with breaking changes:

 - There are no breaking changes in this release.

Packages with other changes:

 - [`moxplatform` - `v0.1.17+2`](#moxplatform---v01172)
 - [`moxplatform_android` - `v0.1.18`](#moxplatform_android---v0118)
 - [`moxplatform_platform_interface` - `v0.1.18`](#moxplatform_platform_interface---v0118)

---

#### `moxplatform` - `v0.1.17+2`

 - **FIX**: Format and lint.

#### `moxplatform_android` - `v0.1.18`

 - **FIX**: Format and lint.
 - **FIX**: Fix self-replies after receiving another message.
 - **FIX**: Add payload to all intents.
 - **FIX**: Fix images disappearing after replying.
 - **FEAT**: Move recordSentMessage to pigeon.
 - **FEAT**: Move the crypto APIs to pigeon.
 - **FEAT**: Adjust to Moxxy changes.
 - **FEAT**: Store the avatar path also in the shared preferences.
 - **FEAT**: Allow the sender's data being null.
 - **FEAT**: Allow attaching arbitrary data to the notification.
 - **FEAT**: Allow showing regular notifications.
 - **FEAT**: Make i18n data a bit more persistent.
 - **FEAT**: Color in the notification silhouette.
 - **FEAT**: Allow setting the self-avatar.
 - **FEAT**: Take care of i18n.

#### `moxplatform_platform_interface` - `v0.1.18`

 - **FIX**: Format and lint.
 - **FIX**: Add payload to all intents.
 - **FEAT**: Move recordSentMessage to pigeon.
 - **FEAT**: Move the crypto APIs to pigeon.
 - **FEAT**: Allow the sender's data being null.
 - **FEAT**: Allow attaching arbitrary data to the notification.
 - **FEAT**: Allow showing regular notifications.
 - **FEAT**: Color in the notification silhouette.
 - **FEAT**: Allow setting the self-avatar.
 - **FEAT**: Take care of i18n.


## 2023-07-21

### Changes

---

Packages with breaking changes:

 - There are no breaking changes in this release.

Packages with other changes:

 - [`moxplatform_android` - `v0.1.17+1`](#moxplatform_android---v01171)
 - [`moxplatform` - `v0.1.17+1`](#moxplatform---v01171)
 - [`moxplatform_platform_interface` - `v0.1.17+1`](#moxplatform_platform_interface---v01171)

Packages with dependency updates only:

> Packages listed below depend on other packages in this workspace that have had changes. Their versions have been incremented to bump the minimum dependency versions of the packages they depend upon in this project.

 - `moxplatform` - `v0.1.17+1`
 - `moxplatform_platform_interface` - `v0.1.17+1`

---

#### `moxplatform_android` - `v0.1.17+1`

 - **FIX**: Accidentally used the name as the target's key. Oops.
 - **FIX**: Fix minor things.


## 2023-07-21

### Changes

---

Packages with breaking changes:

 - There are no breaking changes in this release.

Packages with other changes:

 - [`moxplatform` - `v0.1.17`](#moxplatform---v0117)
 - [`moxplatform_android` - `v0.1.17`](#moxplatform_android---v0117)
 - [`moxplatform_platform_interface` - `v0.1.17`](#moxplatform_platform_interface---v0117)

---

#### `moxplatform` - `v0.1.17`

 - **FIX**: Fix typecasting issue.
 - **FEAT**: Add an API for creating direct share shortcuts.
 - **FEAT**: Migrate to moxlib 0.2.0.
 - **FEAT**: I forgot to bump dependency versions.

#### `moxplatform_android` - `v0.1.17`

 - **FIX**: Fix typecasting issue.
 - **FEAT**: Improve code quality of the cryptography.
 - **FEAT**: Rewrite recordSentMessage in Kotlin.
 - **FEAT**: Add an API for creating direct share shortcuts.
 - **FEAT**: Migrate to moxlib 0.2.0.
 - **FEAT**: I forgot to bump dependency versions.
 - **FEAT**: Also hash the file on encryption and decryption.

#### `moxplatform_platform_interface` - `v0.1.17`

 - **FIX**: Fix typecasting issue.
 - **FEAT**: Add an API for creating direct share shortcuts.
 - **FEAT**: Migrate to moxlib 0.2.0.
 - **FEAT**: I forgot to bump dependency versions.
 - **FEAT**: Also hash the file on encryption and decryption.

