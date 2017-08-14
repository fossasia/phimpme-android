
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import java.lang.String;

// C++: class CvStatModel
/**
 * <p>Base class for statistical models in ML.</p>
 *
 * <p>class CvStatModel <code></p>
 *
 * <p>// C++ code:</p>
 *
 *
 * <p>public:</p>
 *
 * <p>/ * CvStatModel(); * /</p>
 *
 * <p>/ * CvStatModel(const Mat& train_data...); * /</p>
 *
 * <p>virtual ~CvStatModel();</p>
 *
 * <p>virtual void clear()=0;</p>
 *
 * <p>/ * virtual bool train(const Mat& train_data, [int tflag,]..., const</p>
 *
 * <p>Mat& responses,...,</p>
 *
 * <p>[const Mat& var_idx,]..., [const Mat& sample_idx,]...</p>
 *
 * <p>[const Mat& var_type,]..., [const Mat& missing_mask,]</p>
 *
 * <p><misc_training_alg_params>...)=0;</p>
 * <ul>
 *   <li> /
 * </ul>
 *
 * <p>/ * virtual float predict(const Mat& sample...) const=0; * /</p>
 *
 * <p>virtual void save(const char* filename, const char* name=0)=0;</p>
 *
 * <p>virtual void load(const char* filename, const char* name=0)=0;</p>
 *
 * <p>virtual void write(CvFileStorage* storage, const char* name)=0;</p>
 *
 * <p>virtual void read(CvFileStorage* storage, CvFileNode* node)=0;</p>
 *
 * <p>};</p>
 *
 * <p>In this declaration, some methods are commented off. These are methods for
 * which there is no unified API (with the exception of the default
 * constructor). However, there are many similarities in the syntax and
 * semantics that are briefly described below in this section, as if they are
 * part of the base class.
 * </code></p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/statistical_models.html#cvstatmodel">org.opencv.ml.CvStatModel</a>
 */
public class CvStatModel {

    protected final long nativeObj;
    protected CvStatModel(long addr) { nativeObj = addr; }


    //
    // C++:  void CvStatModel::load(c_string filename, c_string name = 0)
    //

/**
 * <p>Loads the model from a file.</p>
 *
 * <p>The method <code>load</code> loads the complete model state with the
 * specified name (or default model-dependent name) from the specified XML or
 * YAML file. The previous model state is cleared by "CvStatModel.clear".</p>
 *
 * @param filename a filename
 * @param name a name
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/statistical_models.html#cvstatmodel-load">org.opencv.ml.CvStatModel.load</a>
 */
    public  void load(String filename, String name)
    {

        load_0(nativeObj, filename, name);

        return;
    }

/**
 * <p>Loads the model from a file.</p>
 *
 * <p>The method <code>load</code> loads the complete model state with the
 * specified name (or default model-dependent name) from the specified XML or
 * YAML file. The previous model state is cleared by "CvStatModel.clear".</p>
 *
 * @param filename a filename
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/statistical_models.html#cvstatmodel-load">org.opencv.ml.CvStatModel.load</a>
 */
    public  void load(String filename)
    {

        load_1(nativeObj, filename);

        return;
    }


    //
    // C++:  void CvStatModel::save(c_string filename, c_string name = 0)
    //

/**
 * <p>Saves the model to a file.</p>
 *
 * <p>The method <code>save</code> saves the complete model state to the specified
 * XML or YAML file with the specified name or default name (which depends on a
 * particular class). *Data persistence* functionality from <code>CxCore</code>
 * is used.</p>
 *
 * @param filename a filename
 * @param name a name
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/statistical_models.html#cvstatmodel-save">org.opencv.ml.CvStatModel.save</a>
 */
    public  void save(String filename, String name)
    {

        save_0(nativeObj, filename, name);

        return;
    }

/**
 * <p>Saves the model to a file.</p>
 *
 * <p>The method <code>save</code> saves the complete model state to the specified
 * XML or YAML file with the specified name or default name (which depends on a
 * particular class). *Data persistence* functionality from <code>CxCore</code>
 * is used.</p>
 *
 * @param filename a filename
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/statistical_models.html#cvstatmodel-save">org.opencv.ml.CvStatModel.save</a>
 */
    public  void save(String filename)
    {

        save_1(nativeObj, filename);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  void CvStatModel::load(c_string filename, c_string name = 0)
    private static native void load_0(long nativeObj, String filename, String name);
    private static native void load_1(long nativeObj, String filename);

    // C++:  void CvStatModel::save(c_string filename, c_string name = 0)
    private static native void save_0(long nativeObj, String filename, String name);
    private static native void save_1(long nativeObj, String filename);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
