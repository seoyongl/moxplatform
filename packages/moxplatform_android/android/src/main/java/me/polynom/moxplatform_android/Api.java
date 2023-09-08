// Autogenerated from Pigeon (v10.1.4), do not edit directly.
// See also: https://pub.dev/packages/pigeon

package me.polynom.moxplatform_android;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.StandardMessageCodec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Generated class from Pigeon. */
@SuppressWarnings({"unused", "unchecked", "CodeBlock2Expr", "RedundantSuppression", "serial"})
public class Api {

  /** Error class for passing custom error details to Flutter via a thrown PlatformException. */
  public static class FlutterError extends RuntimeException {

    /** The error code. */
    public final String code;

    /** The error details. Must be a datatype supported by the api codec. */
    public final Object details;

    public FlutterError(@NonNull String code, @Nullable String message, @Nullable Object details) 
    {
      super(message);
      this.code = code;
      this.details = details;
    }
  }

  @NonNull
  protected static ArrayList<Object> wrapError(@NonNull Throwable exception) {
    ArrayList<Object> errorList = new ArrayList<Object>(3);
    if (exception instanceof FlutterError) {
      FlutterError error = (FlutterError) exception;
      errorList.add(error.code);
      errorList.add(error.getMessage());
      errorList.add(error.details);
    } else {
      errorList.add(exception.toString());
      errorList.add(exception.getClass().getSimpleName());
      errorList.add(
        "Cause: " + exception.getCause() + ", Stacktrace: " + Log.getStackTraceString(exception));
    }
    return errorList;
  }

  public enum CipherAlgorithm {
    AES128GCM_NO_PADDING(0),
    AES256GCM_NO_PADDING(1),
    AES256CBC_PKCS7(2);

    final int index;

    private CipherAlgorithm(final int index) {
      this.index = index;
    }
  }

  public enum FallbackIconType {
    NONE(0),
    PERSON(1),
    NOTES(2);

    final int index;

    private FallbackIconType(final int index) {
      this.index = index;
    }
  }

  public enum FilePickerType {
    IMAGE(0),
    VIDEO(1),
    IMAGE_AND_VIDEO(2),
    GENERIC(3);

    final int index;

    private FilePickerType(final int index) {
      this.index = index;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static final class CryptographyResult {
    private @NonNull byte[] plaintextHash;

    public @NonNull byte[] getPlaintextHash() {
      return plaintextHash;
    }

    public void setPlaintextHash(@NonNull byte[] setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"plaintextHash\" is null.");
      }
      this.plaintextHash = setterArg;
    }

    private @NonNull byte[] ciphertextHash;

    public @NonNull byte[] getCiphertextHash() {
      return ciphertextHash;
    }

    public void setCiphertextHash(@NonNull byte[] setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"ciphertextHash\" is null.");
      }
      this.ciphertextHash = setterArg;
    }

    /** Constructor is non-public to enforce null safety; use Builder. */
    CryptographyResult() {}

    public static final class Builder {

      private @Nullable byte[] plaintextHash;

      public @NonNull Builder setPlaintextHash(@NonNull byte[] setterArg) {
        this.plaintextHash = setterArg;
        return this;
      }

      private @Nullable byte[] ciphertextHash;

      public @NonNull Builder setCiphertextHash(@NonNull byte[] setterArg) {
        this.ciphertextHash = setterArg;
        return this;
      }

      public @NonNull CryptographyResult build() {
        CryptographyResult pigeonReturn = new CryptographyResult();
        pigeonReturn.setPlaintextHash(plaintextHash);
        pigeonReturn.setCiphertextHash(ciphertextHash);
        return pigeonReturn;
      }
    }

    @NonNull
    ArrayList<Object> toList() {
      ArrayList<Object> toListResult = new ArrayList<Object>(2);
      toListResult.add(plaintextHash);
      toListResult.add(ciphertextHash);
      return toListResult;
    }

    static @NonNull CryptographyResult fromList(@NonNull ArrayList<Object> list) {
      CryptographyResult pigeonResult = new CryptographyResult();
      Object plaintextHash = list.get(0);
      pigeonResult.setPlaintextHash((byte[]) plaintextHash);
      Object ciphertextHash = list.get(1);
      pigeonResult.setCiphertextHash((byte[]) ciphertextHash);
      return pigeonResult;
    }
  }

  public interface Result<T> {
    @SuppressWarnings("UnknownNullness")
    void success(T result);

    void error(@NonNull Throwable error);
  }

  private static class MoxplatformApiCodec extends StandardMessageCodec {
    public static final MoxplatformApiCodec INSTANCE = new MoxplatformApiCodec();

    private MoxplatformApiCodec() {}

    @Override
    protected Object readValueOfType(byte type, @NonNull ByteBuffer buffer) {
      switch (type) {
        case (byte) 128:
          return CryptographyResult.fromList((ArrayList<Object>) readValue(buffer));
        default:
          return super.readValueOfType(type, buffer);
      }
    }

    @Override
    protected void writeValue(@NonNull ByteArrayOutputStream stream, Object value) {
      if (value instanceof CryptographyResult) {
        stream.write(128);
        writeValue(stream, ((CryptographyResult) value).toList());
      } else {
        super.writeValue(stream, value);
      }
    }
  }

  /** Generated interface from Pigeon that represents a handler of messages from Flutter. */
  public interface MoxplatformApi {
    /** Platform APIs */
    @NonNull 
    String getPersistentDataPath();

    @NonNull 
    String getCacheDataPath();

    void openBatteryOptimisationSettings();

    @NonNull 
    Boolean isIgnoringBatteryOptimizations();
    /** Contacts APIs */
    void recordSentMessage(@NonNull String name, @NonNull String jid, @Nullable String avatarPath, @NonNull FallbackIconType fallbackIcon);
    /** Cryptography APIs */
    void encryptFile(@NonNull String sourcePath, @NonNull String destPath, @NonNull byte[] key, @NonNull byte[] iv, @NonNull CipherAlgorithm algorithm, @NonNull String hashSpec, @NonNull Result<CryptographyResult> result);

    void decryptFile(@NonNull String sourcePath, @NonNull String destPath, @NonNull byte[] key, @NonNull byte[] iv, @NonNull CipherAlgorithm algorithm, @NonNull String hashSpec, @NonNull Result<CryptographyResult> result);

    void hashFile(@NonNull String sourcePath, @NonNull String hashSpec, @NonNull Result<byte[]> result);
    /** Media APIs */
    @NonNull 
    Boolean generateVideoThumbnail(@NonNull String src, @NonNull String dest, @NonNull Long maxWidth);
    /** Picker */
    void pickFiles(@NonNull FilePickerType type, @NonNull Boolean pickMultiple, @NonNull Result<List<String>> result);

    /** The codec used by MoxplatformApi. */
    static @NonNull MessageCodec<Object> getCodec() {
      return MoxplatformApiCodec.INSTANCE;
    }
    /**Sets up an instance of `MoxplatformApi` to handle messages through the `binaryMessenger`. */
    static void setup(@NonNull BinaryMessenger binaryMessenger, @Nullable MoxplatformApi api) {
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.getPersistentDataPath", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  String output = api.getPersistentDataPath();
                  wrapped.add(0, output);
                }
 catch (Throwable exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  wrapped = wrappedError;
                }
                reply.reply(wrapped);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.getCacheDataPath", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  String output = api.getCacheDataPath();
                  wrapped.add(0, output);
                }
 catch (Throwable exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  wrapped = wrappedError;
                }
                reply.reply(wrapped);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.openBatteryOptimisationSettings", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  api.openBatteryOptimisationSettings();
                  wrapped.add(0, null);
                }
 catch (Throwable exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  wrapped = wrappedError;
                }
                reply.reply(wrapped);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.isIgnoringBatteryOptimizations", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  Boolean output = api.isIgnoringBatteryOptimizations();
                  wrapped.add(0, output);
                }
 catch (Throwable exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  wrapped = wrappedError;
                }
                reply.reply(wrapped);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.recordSentMessage", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                ArrayList<Object> args = (ArrayList<Object>) message;
                String nameArg = (String) args.get(0);
                String jidArg = (String) args.get(1);
                String avatarPathArg = (String) args.get(2);
                FallbackIconType fallbackIconArg = args.get(3) == null ? null : FallbackIconType.values()[(int) args.get(3)];
                try {
                  api.recordSentMessage(nameArg, jidArg, avatarPathArg, fallbackIconArg);
                  wrapped.add(0, null);
                }
 catch (Throwable exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  wrapped = wrappedError;
                }
                reply.reply(wrapped);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.encryptFile", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                ArrayList<Object> args = (ArrayList<Object>) message;
                String sourcePathArg = (String) args.get(0);
                String destPathArg = (String) args.get(1);
                byte[] keyArg = (byte[]) args.get(2);
                byte[] ivArg = (byte[]) args.get(3);
                CipherAlgorithm algorithmArg = args.get(4) == null ? null : CipherAlgorithm.values()[(int) args.get(4)];
                String hashSpecArg = (String) args.get(5);
                Result<CryptographyResult> resultCallback =
                    new Result<CryptographyResult>() {
                      public void success(CryptographyResult result) {
                        wrapped.add(0, result);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.encryptFile(sourcePathArg, destPathArg, keyArg, ivArg, algorithmArg, hashSpecArg, resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.decryptFile", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                ArrayList<Object> args = (ArrayList<Object>) message;
                String sourcePathArg = (String) args.get(0);
                String destPathArg = (String) args.get(1);
                byte[] keyArg = (byte[]) args.get(2);
                byte[] ivArg = (byte[]) args.get(3);
                CipherAlgorithm algorithmArg = args.get(4) == null ? null : CipherAlgorithm.values()[(int) args.get(4)];
                String hashSpecArg = (String) args.get(5);
                Result<CryptographyResult> resultCallback =
                    new Result<CryptographyResult>() {
                      public void success(CryptographyResult result) {
                        wrapped.add(0, result);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.decryptFile(sourcePathArg, destPathArg, keyArg, ivArg, algorithmArg, hashSpecArg, resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.hashFile", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                ArrayList<Object> args = (ArrayList<Object>) message;
                String sourcePathArg = (String) args.get(0);
                String hashSpecArg = (String) args.get(1);
                Result<byte[]> resultCallback =
                    new Result<byte[]>() {
                      public void success(byte[] result) {
                        wrapped.add(0, result);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.hashFile(sourcePathArg, hashSpecArg, resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.generateVideoThumbnail", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                ArrayList<Object> args = (ArrayList<Object>) message;
                String srcArg = (String) args.get(0);
                String destArg = (String) args.get(1);
                Number maxWidthArg = (Number) args.get(2);
                try {
                  Boolean output = api.generateVideoThumbnail(srcArg, destArg, (maxWidthArg == null) ? null : maxWidthArg.longValue());
                  wrapped.add(0, output);
                }
 catch (Throwable exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  wrapped = wrappedError;
                }
                reply.reply(wrapped);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.moxplatform_platform_interface.MoxplatformApi.pickFiles", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                ArrayList<Object> args = (ArrayList<Object>) message;
                FilePickerType typeArg = args.get(0) == null ? null : FilePickerType.values()[(int) args.get(0)];
                Boolean pickMultipleArg = (Boolean) args.get(1);
                Result<List<String>> resultCallback =
                    new Result<List<String>>() {
                      public void success(List<String> result) {
                        wrapped.add(0, result);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.pickFiles(typeArg, pickMultipleArg, resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
    }
  }
}
