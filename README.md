# moxplatform

Platform-specific code for Moxxy. Not intended for use outside of Moxxy.

This repo is based on [very_good_flutter_plugin](https://github.com/VeryGoodOpenSource/very_good_flutter_plugin).

## Development

The development of this package is based on [melos](https://pub.dev/packages/melos).

To make all packages link to each other locally, begin by running `melos bootstrap`. After editing
the code and making your changes, please format the code using `melos run format` and lint using `melos run analyze`.

When done - and a version bump is appropriate - bump the version of all packages using `melos version` and
publish with `melos publish --no-dry-run --git-tag-version`.

## Acknowledgements

- [ekasetiawans](https://github.com/ekasetiawans) for [flutter_background_service](https://github.com/ekasetiawans/flutter_background_service). moxplatform_android's service implementation is basically just a copy and paste of [flutter_background_service](https://github.com/ekasetiawans/flutter_background_service).
