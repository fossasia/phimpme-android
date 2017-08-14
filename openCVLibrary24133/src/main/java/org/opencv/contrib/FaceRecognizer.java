
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.contrib;

import java.lang.String;
import java.util.List;
import org.opencv.core.Algorithm;
import org.opencv.core.Mat;
import org.opencv.utils.Converters;

// C++: class FaceRecognizer
/**
 * <p>All face recognition models in OpenCV are derived from the abstract base
 * class "FaceRecognizer", which provides a unified access to all face
 * recongition algorithms in OpenCV.</p>
 *
 * <p>class FaceRecognizer : public Algorithm <code></p>
 *
 * <p>// C++ code:</p>
 *
 *
 * <p>public:</p>
 *
 * <p>//! virtual destructor</p>
 *
 * <p>virtual ~FaceRecognizer() {}</p>
 *
 * <p>// Trains a FaceRecognizer.</p>
 *
 * <p>virtual void train(InputArray src, InputArray labels) = 0;</p>
 *
 * <p>// Updates a FaceRecognizer.</p>
 *
 * <p>virtual void update(InputArrayOfArrays src, InputArray labels);</p>
 *
 * <p>// Gets a prediction from a FaceRecognizer.</p>
 *
 * <p>virtual int predict(InputArray src) const = 0;</p>
 *
 * <p>// Predicts the label and confidence for a given sample.</p>
 *
 * <p>virtual void predict(InputArray src, int &label, double &confidence) const =
 * 0;</p>
 *
 * <p>// Serializes this object to a given filename.</p>
 *
 * <p>virtual void save(const string& filename) const;</p>
 *
 * <p>// Deserializes this object from a given filename.</p>
 *
 * <p>virtual void load(const string& filename);</p>
 *
 * <p>// Serializes this object to a given cv.FileStorage.</p>
 *
 * <p>virtual void save(FileStorage& fs) const = 0;</p>
 *
 * <p>// Deserializes this object from a given cv.FileStorage.</p>
 *
 * <p>virtual void load(const FileStorage& fs) = 0;</p>
 *
 * <p>// Sets additional information as pairs label - info.</p>
 *
 * <p>void setLabelsInfo(const std.map<int, string>& labelsInfo);</p>
 *
 * <p>// Gets string information by label</p>
 *
 * <p>string getLabelInfo(const int &label);</p>
 *
 * <p>// Gets labels by string</p>
 *
 * <p>vector<int> getLabelsByString(const string& str);</p>
 *
 * <p>};</p>
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/facerec_api.html#facerecognizer">org.opencv.contrib.FaceRecognizer : public Algorithm</a>
 */
public class FaceRecognizer extends Algorithm {

    protected FaceRecognizer(long addr) { super(addr); }


    //
    // C++:  void FaceRecognizer::load(string filename)
    //

/**
 * <p>Loads a "FaceRecognizer" and its model state.</p>
 *
 * <p>Loads a persisted model and state from a given XML or YAML file. Every
 * "FaceRecognizer" has to overwrite <code>FaceRecognizer.load(FileStorage&
 * fs)</code> to enable loading the model state. <code>FaceRecognizer.load(FileStorage&
 * fs)</code> in turn gets called by <code>FaceRecognizer.load(const string&
 * filename)</code>, to ease saving a model.</p>
 *
 * @param filename a filename
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/facerec_api.html#facerecognizer-load">org.opencv.contrib.FaceRecognizer.load</a>
 */
    public  void load(String filename)
    {

        load_0(nativeObj, filename);

        return;
    }


    //
    // C++:  void FaceRecognizer::predict(Mat src, int& label, double& confidence)
    //

/**
 * <p>Predicts a label and associated confidence (e.g. distance) for a given input
 * image.</p>
 *
 * <p>The suffix <code>const</code> means that prediction does not affect the
 * internal model state, so the method can be safely called from within
 * different threads.</p>
 *
 * <p>The following example shows how to get a prediction from a trained model:
 * using namespace cv; <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// Do your initialization here (create the cv.FaceRecognizer model)...</p>
 *
 * <p>//...</p>
 *
 * <p>// Read in a sample image:</p>
 *
 * <p>Mat img = imread("person1/3.jpg", CV_LOAD_IMAGE_GRAYSCALE);</p>
 *
 * <p>// And get a prediction from the cv.FaceRecognizer:</p>
 *
 * <p>int predicted = model->predict(img);</p>
 *
 * <p>Or to get a prediction and the associated confidence (e.g. distance): </code></p>
 *
 * <p>using namespace cv; <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// Do your initialization here (create the cv.FaceRecognizer model)...</p>
 *
 * <p>//...</p>
 *
 * <p>Mat img = imread("person1/3.jpg", CV_LOAD_IMAGE_GRAYSCALE);</p>
 *
 * <p>// Some variables for the predicted label and associated confidence (e.g.
 * distance):</p>
 *
 * <p>int predicted_label = -1;</p>
 *
 * <p>double predicted_confidence = 0.0;</p>
 *
 * <p>// Get the prediction and associated confidence from the model</p>
 *
 * <p>model->predict(img, predicted_label, predicted_confidence);</p>
 *
 * @param src Sample image to get a prediction from.
 * @param label The predicted label for the given image.
 * @param confidence Associated confidence (e.g. distance) for the predicted
 * label.
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/facerec_api.html#facerecognizer-predict">org.opencv.contrib.FaceRecognizer.predict</a>
 */
    public  void predict(Mat src, int[] label, double[] confidence)
    {
        double[] label_out = new double[1];
        double[] confidence_out = new double[1];
        predict_0(nativeObj, src.nativeObj, label_out, confidence_out);
        if(label!=null) label[0] = (int)label_out[0];
        if(confidence!=null) confidence[0] = (double)confidence_out[0];
        return;
    }


    //
    // C++:  void FaceRecognizer::save(string filename)
    //

/**
 * <p>Saves a "FaceRecognizer" and its model state.</p>
 *
 * <p>Saves this model to a given filename, either as XML or YAML.</p>
 *
 * <p>Saves this model to a given "FileStorage".</p>
 *
 * <p>Every "FaceRecognizer" overwrites <code>FaceRecognizer.save(FileStorage&
 * fs)</code> to save the internal model state. <code>FaceRecognizer.save(const
 * string& filename)</code> saves the state of a model to the given filename.</p>
 *
 * <p>The suffix <code>const</code> means that prediction does not affect the
 * internal model state, so the method can be safely called from within
 * different threads.</p>
 *
 * @param filename The filename to store this "FaceRecognizer" to (either
 * XML/YAML).
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/facerec_api.html#facerecognizer-save">org.opencv.contrib.FaceRecognizer.save</a>
 */
    public  void save(String filename)
    {

        save_0(nativeObj, filename);

        return;
    }


    //
    // C++:  void FaceRecognizer::train(vector_Mat src, Mat labels)
    //

/**
 * <p>Trains a FaceRecognizer with given data and associated labels.</p>
 *
 * <p>The following source code snippet shows you how to learn a Fisherfaces model
 * on a given set of images. The images are read with "imread" and pushed into a
 * <code>std.vector<Mat></code>. The labels of each image are stored within a
 * <code>std.vector<int></code> (you could also use a "Mat" of type
 * "CV_32SC1"). Think of the label as the subject (the person) this image
 * belongs to, so same subjects (persons) should have the same label. For the
 * available "FaceRecognizer" you don't have to pay any attention to the order
 * of the labels, just make sure same persons have the same label: // holds
 * images and labels <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>vector<Mat> images;</p>
 *
 * <p>vector<int> labels;</p>
 *
 * <p>// images for first person</p>
 *
 * <p>images.push_back(imread("person0/0.jpg", CV_LOAD_IMAGE_GRAYSCALE));
 * labels.push_back(0);</p>
 *
 * <p>images.push_back(imread("person0/1.jpg", CV_LOAD_IMAGE_GRAYSCALE));
 * labels.push_back(0);</p>
 *
 * <p>images.push_back(imread("person0/2.jpg", CV_LOAD_IMAGE_GRAYSCALE));
 * labels.push_back(0);</p>
 *
 * <p>// images for second person</p>
 *
 * <p>images.push_back(imread("person1/0.jpg", CV_LOAD_IMAGE_GRAYSCALE));
 * labels.push_back(1);</p>
 *
 * <p>images.push_back(imread("person1/1.jpg", CV_LOAD_IMAGE_GRAYSCALE));
 * labels.push_back(1);</p>
 *
 * <p>images.push_back(imread("person1/2.jpg", CV_LOAD_IMAGE_GRAYSCALE));
 * labels.push_back(1);</p>
 *
 * <p>Now that you have read some images, we can create a new "FaceRecognizer". In
 * this example I'll create a Fisherfaces model and decide to keep all of the
 * possible Fisherfaces: </code></p>
 *
 * <p>// Create a new Fisherfaces model and retain all available Fisherfaces,
 * <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// this is the most common usage of this specific FaceRecognizer:</p>
 *
 * <p>//</p>
 *
 * <p>Ptr<FaceRecognizer> model = createFisherFaceRecognizer();</p>
 *
 * <p>And finally train it on the given dataset (the face images and labels):
 * </code></p>
 *
 * <p>// This is the common interface to train all of the available
 * cv.FaceRecognizer <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// implementations:</p>
 *
 * <p>//</p>
 *
 * <p>model->train(images, labels);</p>
 *
 * @param src The training images, that means the faces you want to learn. The
 * data has to be given as a <code>vector<Mat></code>.
 * @param labels The labels corresponding to the images have to be given either
 * as a <code>vector<int></code> or a
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/facerec_api.html#facerecognizer-train">org.opencv.contrib.FaceRecognizer.train</a>
 */
    public  void train(List<Mat> src, Mat labels)
    {
        Mat src_mat = Converters.vector_Mat_to_Mat(src);
        train_0(nativeObj, src_mat.nativeObj, labels.nativeObj);

        return;
    }


    //
    // C++:  void FaceRecognizer::update(vector_Mat src, Mat labels)
    //

/**
 * <p>Updates a FaceRecognizer with given data and associated labels.</p>
 *
 * <p>This method updates a (probably trained) "FaceRecognizer", but only if the
 * algorithm supports it. The Local Binary Patterns Histograms (LBPH) recognizer
 * (see "createLBPHFaceRecognizer") can be updated. For the Eigenfaces and
 * Fisherfaces method, this is algorithmically not possible and you have to
 * re-estimate the model with "FaceRecognizer.train". In any case, a call to
 * train empties the existing model and learns a new model, while update does
 * not delete any model data.
 * // Create a new LBPH model (it can be updated) and use the default
 * parameters, <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// this is the most common usage of this specific FaceRecognizer:</p>
 *
 * <p>//</p>
 *
 * <p>Ptr<FaceRecognizer> model = createLBPHFaceRecognizer();</p>
 *
 * <p>// This is the common interface to train all of the available
 * cv.FaceRecognizer</p>
 *
 * <p>// implementations:</p>
 *
 * <p>//</p>
 *
 * <p>model->train(images, labels);</p>
 *
 * <p>// Some containers to hold new image:</p>
 *
 * <p>vector<Mat> newImages;</p>
 *
 * <p>vector<int> newLabels;</p>
 *
 * <p>// You should add some images to the containers:</p>
 *
 * <p>//</p>
 *
 * <p>//...</p>
 *
 * <p>//</p>
 *
 * <p>// Now updating the model is as easy as calling:</p>
 *
 * <p>model->update(newImages,newLabels);</p>
 *
 * <p>// This will preserve the old model data and extend the existing model</p>
 *
 * <p>// with the new features extracted from newImages!</p>
 *
 * <p>Calling update on an Eigenfaces model (see "createEigenFaceRecognizer"),
 * which doesn't support updating, will throw an error similar to: </code></p>
 *
 * <p>OpenCV Error: The function/feature is not implemented (This FaceRecognizer
 * (FaceRecognizer.Eigenfaces) does not support updating, you have to use
 * FaceRecognizer.train to update it.) in update, file /home/philipp/git/opencv/modules/contrib/src/facerec.cpp,
 * line 305 <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>terminate called after throwing an instance of 'cv.Exception'</p>
 *
 * <p>Please note: The "FaceRecognizer" does not store your training images,
 * because this would be very memory intense and it's not the responsibility of
 * te "FaceRecognizer" to do so. The caller is responsible for maintaining the
 * dataset, he want to work with.
 * </code></p>
 *
 * @param src The training images, that means the faces you want to learn. The
 * data has to be given as a <code>vector<Mat></code>.
 * @param labels The labels corresponding to the images have to be given either
 * as a <code>vector<int></code> or a
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/facerec_api.html#facerecognizer-update">org.opencv.contrib.FaceRecognizer.update</a>
 */
    public  void update(List<Mat> src, Mat labels)
    {
        Mat src_mat = Converters.vector_Mat_to_Mat(src);
        update_0(nativeObj, src_mat.nativeObj, labels.nativeObj);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  void FaceRecognizer::load(string filename)
    private static native void load_0(long nativeObj, String filename);

    // C++:  void FaceRecognizer::predict(Mat src, int& label, double& confidence)
    private static native void predict_0(long nativeObj, long src_nativeObj, double[] label_out, double[] confidence_out);

    // C++:  void FaceRecognizer::save(string filename)
    private static native void save_0(long nativeObj, String filename);

    // C++:  void FaceRecognizer::train(vector_Mat src, Mat labels)
    private static native void train_0(long nativeObj, long src_mat_nativeObj, long labels_nativeObj);

    // C++:  void FaceRecognizer::update(vector_Mat src, Mat labels)
    private static native void update_0(long nativeObj, long src_mat_nativeObj, long labels_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
