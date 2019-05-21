package org.fossasia.phimpme.opencamera.Camera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/** Handles gyro sensor. */
public class GyroSensor implements SensorEventListener {
  private static final String TAG = "GyroSensor";

  private final SensorManager mSensorManager;
  private final Sensor mSensor;

  private boolean is_recording;
  private long timestamp;

  private static final float NS2S = 1.0f / 1000000000.0f;
  private final float[] deltaRotationVector = new float[4];
  private final float[] currentRotationMatrix = new float[9];
  private final float[] deltaRotationMatrix = new float[9];
  private final float[] tempMatrix = new float[9];

  private final float[] tempVector = new float[3];
  private final float[] inVector = new float[3];

  public interface TargetCallback {
    void onAchieved();
  }

  private boolean hasTarget;
  private final float[] targetVector = new float[3];
  private float targetAngle; // target angle in radians
  private TargetCallback targetCallback;

  GyroSensor(Context context) {
    mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    if (MyDebug.LOG) {
      Log.d(TAG, "GyroSensor");
      if (mSensor == null) Log.d(TAG, "gyroscope not available");
    }
    setToIdentity();
  }

  private void setToIdentity() {
    for (int i = 0; i < 9; i++) {
      currentRotationMatrix[i] = 0.0f;
    }
    currentRotationMatrix[0] = 1.0f;
    currentRotationMatrix[4] = 1.0f;
    currentRotationMatrix[8] = 1.0f;
  }

  /** Helper method to set a 3D vector. */
  private void setVector(final float[] vector, float x, float y, float z) {
    vector[0] = x;
    vector[1] = y;
    vector[2] = z;
  }

  /** Helper method to access the (i, j)th component of a 3x3 matrix. */
  private float getMatrixComponent(final float[] matrix, int row, int col) {
    return matrix[row * 3 + col];
  }

  /** Helper method to set the (i, j)th component of a 3x3 matrix. */
  private void setMatrixComponent(final float[] matrix, int row, int col, float value) {
    matrix[row * 3 + col] = value;
  }

  /** Helper method to multiply 3x3 matrix with a 3D vector. */
  private void transformVector(final float[] result, final float[] matrix, final float[] vector) {
    // result[i] = matrix[ij] . vector[j]
    for (int i = 0; i < 3; i++) {
      result[i] = 0.0f;
      for (int j = 0; j < 3; j++) {
        result[i] += getMatrixComponent(matrix, i, j) * vector[j];
      }
    }
  }

  /**
   * Helper method to multiply the transpose of a 3x3 matrix with a 3D vector. For 3x3 rotation
   * (orthonormal) matrices, the transpose is the inverse.
   */
  private void transformTransposeVector(
      final float[] result, final float[] matrix, final float[] vector) {
    // result[i] = matrix[ji] . vector[j]
    for (int i = 0; i < 3; i++) {
      result[i] = 0.0f;
      for (int j = 0; j < 3; j++) {
        result[i] += getMatrixComponent(matrix, j, i) * vector[j];
      }
    }
  }

  void startRecording() {
    if (MyDebug.LOG) Log.d(TAG, "startRecording");
    is_recording = true;
    timestamp = 0;
    setToIdentity();
    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  void stopRecording() {
    if (is_recording) {
      if (MyDebug.LOG) Log.d(TAG, "stopRecording");
      is_recording = false;
      timestamp = 0;
      mSensorManager.unregisterListener(this);
    }
  }

  public boolean isRecording() {
    return this.is_recording;
  }

  void setTarget(
      float target_x,
      float target_y,
      float target_z,
      float targetAngle,
      TargetCallback targetCallback) {
    this.hasTarget = true;
    this.targetVector[0] = target_x;
    this.targetVector[1] = target_y;
    this.targetVector[2] = target_z;
    this.targetAngle = targetAngle;
    this.targetCallback = targetCallback;
  }

  void clearTarget() {
    this.hasTarget = false;
    this.targetCallback = null;
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @Override
  public void onSensorChanged(SensorEvent event) {
    // This timestep's delta rotation to be multiplied by the current rotation
    // after computing it from the gyro sample data.
    if (timestamp != 0) {
      final float dT = (event.timestamp - timestamp) * NS2S;
      // Axis of the rotation sample, not normalized yet.
      float axisX = event.values[0];
      float axisY = event.values[1];
      float axisZ = event.values[2];

      // Calculate the angular speed of the sample
      double omegaMagnitude = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

      // Normalize the rotation vector if it's big enough to get the axis
      // (that is, EPSILON should represent your maximum allowable margin of error)
      if (omegaMagnitude > 1.0e-5) {
        axisX /= omegaMagnitude;
        axisY /= omegaMagnitude;
        axisZ /= omegaMagnitude;
      }

      // Integrate around this axis with the angular speed by the timestep
      // in order to get a delta rotation from this sample over the timestep
      // We will convert this axis-angle representation of the delta rotation
      // into a quaternion before turning it into the rotation matrix.
      double thetaOverTwo = omegaMagnitude * dT / 2.0f;
      float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
      float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
      deltaRotationVector[0] = sinThetaOverTwo * axisX;
      deltaRotationVector[1] = sinThetaOverTwo * axisY;
      deltaRotationVector[2] = sinThetaOverTwo * axisZ;
      deltaRotationVector[3] = cosThetaOverTwo;

      SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
      // User code should concatenate the delta rotation we computed with the current rotation
      // in order to get the updated rotation.
      // currentRotationMatrix = currentRotationMatrix * deltaRotationMatrix;
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          float value = 0.0f;
          // tempMatrix[ij] = currentRotationMatrix[ik] * deltaRotationMatrix[kj]
          for (int k = 0; k < 3; k++) {
            value +=
                getMatrixComponent(currentRotationMatrix, i, k)
                    * getMatrixComponent(deltaRotationMatrix, k, j);
          }
          setMatrixComponent(tempMatrix, i, j, value);
        }
      }

      System.arraycopy(tempMatrix, 0, currentRotationMatrix, 0, 9);

      if (MyDebug.LOG) {
        setVector(inVector, 0.0f, 0.0f, -1.0f); // vector pointing behind the device's screen
        transformVector(tempVector, currentRotationMatrix, inVector);
        // transformTransposeVector(tempVector, currentRotationMatrix, inVector);
        Log.d(
            TAG,
            "### gyro vector: " + tempVector[0] + " , " + tempVector[1] + " , " + tempVector[2]);
      }

      if (hasTarget) {
        setVector(inVector, 0.0f, 0.0f, -1.0f); // vector pointing behind the device's screen
        transformVector(tempVector, currentRotationMatrix, inVector);
        float cos_angle =
            tempVector[0] * targetVector[0]
                + tempVector[1] * targetVector[1]
                + tempVector[2] * targetVector[2];
        float angle = (float) Math.acos(cos_angle);
        if (MyDebug.LOG)
          Log.d(TAG, "gyro vector angle with target: " + Math.toDegrees(angle) + " degrees");
        if (angle <= targetAngle) {
          targetCallback.onAchieved();
        }
      }
    }

    timestamp = event.timestamp;
  }

  /*  This returns a 3D vector, that represents the current direction that the device is pointing (looking towards the screen),
   *  relative to when startRecording() was called.
   *  That is, the coordinate system is defined by the device's initial orientation when startRecording() was called:
   *      X: -ve to +ve is left to right
   *      Y: -ve to +ve is down to up
   *      Z: -ve to +ve is out of the screen to behind the screen
   *  So if the device hasn't changed orientation, this will return (0, 0, -1).
   *  (1, 0, 0) means the device has rotated 90 degrees so it's now pointing to the right.
   * @param result An array of length 3 to store the returned vector.
   */
  /*void getRelativeVector(float [] result) {
  	setVector(inVector, 0.0f, 0.0f, -1.0f); // vector pointing behind the device's screen
  	transformVector(result, currentRotationMatrix, inVector);
  }*/

  /*void getRelativeInverseVector(float [] result) {
  	setVector(inVector, 0.0f, 0.0f, -1.0f); // vector pointing behind the device's screen
  	transformTransposeVector(result, currentRotationMatrix, inVector);
  }*/

  public void getRelativeInverseVector(float[] out, float[] in) {
    transformTransposeVector(out, currentRotationMatrix, in);
  }
}
