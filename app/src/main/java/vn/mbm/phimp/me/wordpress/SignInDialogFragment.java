package vn.mbm.phimp.me.wordpress;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

import vn.mbm.phimp.me.MyApplication;
import vn.mbm.phimp.me.R;

public class SignInDialogFragment extends DialogFragment {
    private static String ARG_TITLE = "title";
    private static String ARG_DESCRIPTION = "message";
    private static String ARG_FOOTER = "footer";
    private static String ARG_IMAGE = "image";
    private static String ARG_NUMBER_OF_BUTTONS = "number-of-buttons";
    private static String ARG_FIRST_BUTTON_LABEL = "first-btn-label";
    private static String ARG_SECOND_BUTTON_LABEL = "second-btn-label";
    private static String ARG_THIRD_BUTTON_LABEL = "third-btn-label";
    private static String ARG_FIRST_BUTTON_ACTION = "first-btn-action";
    private static String ARG_SECOND_BUTTON_ACTION = "second-btn-action";
    private static String ARG_THIRD_BUTTON_ACTION = "third-btn-action";
    private static String ARG_TELL_ME_MORE_BUTTON_ACTION = "tell-me-more-btn-action";
    private static String ARG_TELL_ME_MORE_BUTTON_PARAM_NAME_FAQ_ID = "tell-me-more-btn-param-name-faq-id";
    private static String ARG_TELL_ME_MORE_BUTTON_PARAM_NAME_SECTION_ID = "tell-me-more-btn-param-name-section-id";

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mFooterBottomButton;
    private TextView mFooterCenterButton;

    public static final int ACTION_FINISH = 1;

    public SignInDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).component().inject(this);
    }

    public static SignInDialogFragment newInstance(String title, String message, int imageSource, String buttonLabel) {
        return newInstance(title, message, imageSource, 1, buttonLabel, "", "", 0, 0, 0, "", "");
    }

    public static SignInDialogFragment newInstance(String title, String message, int imageSource, int numberOfButtons,
                                                   String firstButtonLabel, String secondButtonLabel,
                                                   String thirdButtonLabel, int secondButtonAction,
                                                   int thirdButtonAction) {
        return newInstance(title, message, imageSource, numberOfButtons, firstButtonLabel, secondButtonLabel,
                thirdButtonLabel, 0, secondButtonAction, thirdButtonAction, "", "");
    }

    public static SignInDialogFragment newInstance(String title, String message, int imageSource, int numberOfButtons,
                                                   String firstButtonLabel, String secondButtonLabel,
                                                   String thirdButtonLabel, int firstButtonAction,
                                                   int secondButtonAction,
                                                   int thirdButtonAction,
                                                   String tellMeMoreButtonFaqId,
                                                   String tellMeMoreButtonSectionId) {
        SignInDialogFragment adf = new SignInDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_DESCRIPTION, message);
        bundle.putInt(ARG_IMAGE, imageSource);
        bundle.putInt(ARG_NUMBER_OF_BUTTONS, numberOfButtons);
        bundle.putString(ARG_FIRST_BUTTON_LABEL, firstButtonLabel);
        bundle.putString(ARG_SECOND_BUTTON_LABEL, secondButtonLabel);
        bundle.putString(ARG_THIRD_BUTTON_LABEL, thirdButtonLabel);
        bundle.putInt(ARG_FIRST_BUTTON_ACTION, firstButtonAction);
        bundle.putInt(ARG_SECOND_BUTTON_ACTION, secondButtonAction);
        bundle.putInt(ARG_THIRD_BUTTON_ACTION, thirdButtonAction);
        bundle.putString(ARG_TELL_ME_MORE_BUTTON_PARAM_NAME_FAQ_ID, tellMeMoreButtonFaqId);
        bundle.putString(ARG_TELL_ME_MORE_BUTTON_PARAM_NAME_SECTION_ID, tellMeMoreButtonSectionId);

        adf.setArguments(bundle);
        adf.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme);
        return adf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.grey));
        View v = inflater.inflate(R.layout.signin_dialog_fragment, container, false);

        mImageView = (ImageView) v.findViewById(R.id.nux_dialog_image);
        mTitleTextView = (TextView) v.findViewById(R.id.nux_dialog_title);
        mDescriptionTextView = (TextView) v.findViewById(R.id.nux_dialog_description);
        mFooterBottomButton = (TextView) v.findViewById(R.id.nux_dialog_first_button);
//        mFooterCenterButton = (TextView) v.findViewById(R.id.nux_dialog_second_button);
        final Bundle arguments = getArguments();

        mTitleTextView.setText(arguments.getString(ARG_TITLE));
        mDescriptionTextView.setText(arguments.getString(ARG_DESCRIPTION));
        mImageView.setImageResource(arguments.getInt(ARG_IMAGE));

        View.OnClickListener clickListenerDismiss = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        };

        View.OnClickListener clickListenerSecondButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAction(v, arguments.getInt(ARG_SECOND_BUTTON_ACTION, 0), arguments);
            }
        };

        View.OnClickListener clickListenerThirdButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAction(v, arguments.getInt(ARG_THIRD_BUTTON_ACTION, 0), arguments);
            }
        };

        switch (arguments.getInt(ARG_NUMBER_OF_BUTTONS, 1)) {
            case 1:
                // One button: we keep only the centered button
                mFooterCenterButton.setText(arguments.getString(ARG_FIRST_BUTTON_LABEL));
                mFooterCenterButton.setOnClickListener(clickListenerDismiss);
                mFooterBottomButton.setVisibility(View.GONE);
                break;
            case 2:
                // Two buttons: we keep only the left and right buttons
                mFooterBottomButton.setText(arguments.getString(ARG_FIRST_BUTTON_LABEL));
                mFooterCenterButton.setVisibility(View.GONE);
                break;
            case 3:
                mFooterBottomButton.setText(arguments.getString(ARG_FIRST_BUTTON_LABEL));
                mFooterCenterButton.setText(arguments.getString(ARG_SECOND_BUTTON_LABEL));
                mFooterCenterButton.setOnClickListener(clickListenerSecondButton);
                break;
        }
        v.setClickable(true);
        v.setOnClickListener(clickListenerDismiss);
        mFooterBottomButton.setOnClickListener(clickListenerDismiss);

        return v;
    }

    private void onClickAction(View v, int action, Bundle arguments) {
        if (!isAdded()) {
            return;
        }
        switch (action) {
            default:
            case ACTION_FINISH:
                getActivity().finish();
                break;
        }
    }
}
