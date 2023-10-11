package com.zhd.shj.camare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraHelper {

    private Context context;
    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private Size[] previewSizes; // 用于存储支持的预览尺寸
    TextureView textureView;

    public CameraHelper(Context context, TextureView mtextureView) {
        this.context = context;
        textureView=mtextureView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
        cameraId = chooseCamera();
    }

    private String chooseCamera() {
        // 在这里选择使用哪个摄像头，例如 "0" 表示后置摄像头，"1" 表示前置摄像头
        // 这里简单地选择第一个摄像头
        String[] cameraIds = new String[0];
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraIds = cameraManager.getCameraIdList();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return cameraIds.length > 0 ? cameraIds[0] : null;
    }

    public void openCamera() {
        try {
            // 打开相机
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(cameraId, cameraStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreviewSession(); // 在相机打开后创建预览会话
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void setUpCamera() {
        try {
            // 获取相机特性
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            // 获取支持的预览尺寸
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
                previewSizes = map.getOutputSizes(SurfaceTexture.class);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {
        try {
            if (previewSizes == null || previewSizes.length == 0 || cameraDevice == null) {
                // 如果支持的预览尺寸为空或相机设备为空，则无法继续进行预览
                return;
            }

            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            assert surfaceTexture != null;

            // 设置预览尺寸（根据需要进行调整）
            Size previewSize = chooseOptimalSize(previewSizes, textureView.getWidth(), textureView.getHeight());
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            Surface previewSurface = new Surface(surfaceTexture);
            final CaptureRequest.Builder captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (cameraDevice == null) {
                                // 相机已经关闭，不执行后续操作
                                return;
                            }

                            cameraCaptureSession = session;
                            try {
                                cameraCaptureSession.setRepeatingRequest(
                                        captureRequestBuilder.build(),
                                        null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // 配置预览会话失败的处理
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }


    }

    private Size chooseOptimalSize(Size[] choices, int width, int height) {
        // 在这里实现你选择预览尺寸的逻辑
        // 可以根据需求选择最适合的尺寸，比如选择与 TextureView 大小最接近的尺寸

        // 示例代码：选择与 TextureView 大小最接近的尺寸
        Size optimalSize = null;
        double aspectRatio = (double) width / height;
        double minAspectRatioDiff = Double.MAX_VALUE;
        for (Size size : choices) {
            double sizeAspectRatio = (double) size.getWidth() / size.getHeight();
            double aspectRatioDiff = Math.abs(sizeAspectRatio - aspectRatio);
            if (aspectRatioDiff < minAspectRatioDiff) {
                optimalSize = size;
                minAspectRatioDiff = aspectRatioDiff;
            }
        }
        return optimalSize;
    }

    // ... 其他方法
}
