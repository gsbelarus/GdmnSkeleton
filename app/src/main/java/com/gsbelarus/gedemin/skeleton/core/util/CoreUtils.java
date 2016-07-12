package com.gsbelarus.gedemin.skeleton.core.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.data.SQLiteDataType;
import com.gsbelarus.gedemin.skeleton.core.view.TextWatcherAdapter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class CoreUtils {

    private static final AtomicInteger nextGeneratedViewId = new AtomicInteger(1);

    @NonNull
    public static CharSequence getFieldValueString(int columnIndex, Cursor dataCursor) {
        CharSequence value = "";
        switch (dataCursor.getType(columnIndex)) {
            case Cursor.FIELD_TYPE_STRING:
                value = dataCursor.getString(columnIndex);
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                value = String.valueOf(dataCursor.getInt(columnIndex));
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                value = String.valueOf(dataCursor.getFloat(columnIndex));
                break;
            case Cursor.FIELD_TYPE_NULL:
                SpannableString nullString = new SpannableString("[null]");
                nullString.setSpan(new StyleSpan(Typeface.ITALIC), 0, nullString.length(), 0);
                value = nullString;
                break;
            case Cursor.FIELD_TYPE_BLOB:
                value =  String.format("(%d bytes)", dataCursor.getBlob(columnIndex).length);
                break;
            default:
                throw new AssertionError("Unknown type: " + dataCursor.getType(columnIndex));
        }

        if (dataCursor.isNull(columnIndex)) {
            value = "";
        }

        return value;
    }

    public static int generateViewId() {
        int result;
        int newValue;
        do {
            result = nextGeneratedViewId.get();
            newValue = result + 1;
            if (newValue > ViewCompat.MEASURED_SIZE_MASK) {
                newValue = 1;
            }
        } while (!nextGeneratedViewId.compareAndSet(result, newValue));

        return result;
    }

    public static int dpToPixel(float dp, Context context) {
        return (int) (dp * ((float)  context.getResources().
                getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /////////////////////////////////////////////

    public enum CoreViewType { EDIT_VIEW, DATA_VIEW, LABELED_DATA_VIEW }

    public static LinkedHashMap<View, View> includeCoreView(
            @LayoutRes final int rowLayoutRes,
            ViewGroup parent,
            final int columnCount,
            final CoreViewType coreViewType,
            @Nullable final View.OnKeyListener onKeyBackListener,
            @Nullable final TextWatcher editTextWatcher) { //TODO return object

        LinkedHashMap<View, View> valueViewLabelViewMap = new LinkedHashMap<>();

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LinearLayout itemView = (LinearLayout) layoutInflater.inflate(rowLayoutRes, parent, false);
        Context context = parent.getContext();

        itemView.removeAllViews(); //

        LinearLayout dynamicLinear = new LinearLayout(context);
        dynamicLinear.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < columnCount; i++) {
            View rowView = null;
            View valueView = null;
            View labelView = null;

            if (coreViewType == CoreViewType.DATA_VIEW || coreViewType == CoreViewType.LABELED_DATA_VIEW) {
                rowView = layoutInflater.inflate(R.layout.core_recycler_item_tv, dynamicLinear, false);
                valueView = rowView.findViewById(R.id.core_recycler_item_value);

                if (coreViewType == CoreViewType.LABELED_DATA_VIEW) {
                    labelView = rowView.findViewById(R.id.core_recycler_item_label);
                }
            } else if (coreViewType == CoreViewType.EDIT_VIEW) {
                rowView = layoutInflater.inflate(R.layout.core_edit_item_et, dynamicLinear, false);
                valueView = rowView.findViewById(R.id.core_edit_item_et);
                labelView = rowView.findViewById(R.id.core_edit_item_input);

                int imeOptions = ((EditText)valueView).getImeOptions();
                boolean imeContainsActionNext = (imeOptions != ((imeOptions & ~EditorInfo.IME_MASK_ACTION) & ~EditorInfo.IME_ACTION_NEXT));

                if ((i == columnCount-1) && imeContainsActionNext) {
                    imeOptions = ((imeOptions & ~EditorInfo.IME_MASK_ACTION) & ~EditorInfo.IME_ACTION_NEXT) | EditorInfo.IME_ACTION_DONE;
                    ((EditText) valueView).setImeOptions(imeOptions);
                }

                if (onKeyBackListener != null) valueView.setOnKeyListener(onKeyBackListener);
                if (editTextWatcher != null) ((EditText) valueView).addTextChangedListener(editTextWatcher);
            }

            valueViewLabelViewMap.put(valueView, labelView);

            int viewId = generateViewId();
            rowView.setId(viewId);

            dynamicLinear.addView(rowView);
        }

        itemView.addView(dynamicLinear);
        parent.addView(itemView);
        return valueViewLabelViewMap;
    }

    public static LinkedHashMap<View, View> includeCoreDetailView(ViewGroup parent, final int columnCount) {
        parent.removeAllViewsInLayout(); //TODO --test
        return includeCoreView(R.layout.core_detail_item, parent, columnCount, CoreViewType.LABELED_DATA_VIEW, null, null);
    }

    public static LinkedHashMap<View, View> includeCoreEditView(ViewGroup parent, int columnCount, View.OnKeyListener onKeyBackListener, final TextWatcher editTextWatcher) {
        parent.removeAllViewsInLayout(); //TODO --test
        return includeCoreView(R.layout.core_edit_item, parent, columnCount, CoreViewType.EDIT_VIEW, onKeyBackListener, editTextWatcher);
    }

    public static void bindViews(@Nullable Cursor cursor, final String[] originalFrom, Map<View, View> toValueViewLabelViewMap) {
        if (cursor == null || cursor.getCount() == 0) return;

        int[] from = new int[originalFrom.length];
        for (int i = 0; i < originalFrom.length; i++) {
            from[i] = cursor.getColumnIndex(originalFrom[i]);
        }

        bindViews(cursor, from, toValueViewLabelViewMap);
    }

    public static void bindViews(@Nullable Cursor cursor, final int[] from, Map<View, View> toValueViewLabelViewMap) {
        if (cursor == null || cursor.getCount() == 0) return;

        int i = 0;
        for (Map.Entry<View, View> entry : toValueViewLabelViewMap.entrySet()) {
            final View labelView = entry.getValue();
            View valueView = entry.getKey();
            int columnIndex = from[i];

            if (labelView != null) {
                if (labelView instanceof TextView) {
                    bindLabelTextView((TextView) labelView,  cursor.getColumnName(columnIndex));
                } else if (labelView instanceof TextInputLayout) {
                    bindTextInputLayout((TextInputLayout)labelView, cursor.getColumnName(columnIndex));
                } else {
                    throw new IllegalStateException(valueView.getClass().getName() + " is not a view that can be bounds by this CoreCursorRecyclerViewAdapter");
                }
            }

            if (valueView instanceof EditText) {
                CharSequence hint = null;
                TextWatcherAdapter notNullInputValidator = null; //TODO if have constraint NOT NULL !
                if (labelView == null) {
                    hint = cursor.getColumnName(columnIndex);
                } else if (labelView instanceof TextInputLayout) {
//                    notNullInputValidator = new TextWatcherAdapter() {
//                        @Override
//                        public void onTextChanged(String text) {
//                            ((TextInputLayout) labelView).setError(isValide(text) ? null : "Данное поле не может быть пустым");
//                        }
//
//                        private boolean isValide(String text) {
//                            return !TextUtils.isEmpty(text);
//                        }
//                    };
                }

                bindEditText((EditText) valueView, CoreUtils.getFieldValueString(columnIndex, cursor), hint, cursor.getType(columnIndex), notNullInputValidator);

            } else if (valueView instanceof TextView) {
                bindTextView((TextView) valueView, CoreUtils.getFieldValueString(columnIndex, cursor));

            } else if (valueView instanceof ImageView) {
                bindImageView((ImageView) valueView, cursor.getBlob(columnIndex));

            } else {
                throw new IllegalStateException(valueView.getClass().getName() + " is not a view that can be bounds by this CoreCursorRecyclerViewAdapter");
            }

            i++;
        }
    }

    private static void bindLabelTextView(TextView labelView, CharSequence value) {
        labelView.setText(String.format("%s:", value));
    }

    private static void bindTextView(TextView textView, CharSequence value) {
        textView.setText(value);
    }

    private static void bindImageView(ImageView imageView, byte[] value) {
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(value, 0, value.length));
    }

    private static void bindEditText(EditText editText, @Nullable CharSequence value, @Nullable CharSequence hint, int columnType, @Nullable TextWatcher textWatcher) {
        editText.setText(value);
        editText.setHint(hint);
        if (textWatcher != null) editText.addTextChangedListener(textWatcher);

        switch (SQLiteDataType.SQLiteDataTypes.values()[columnType]) {
            case INTEGER :
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                break;
            case FLOAT :
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case STRING :
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    private static void bindTextInputLayout(final TextInputLayout textInputLayout, CharSequence hint) {
        textInputLayout.setHintAnimationEnabled(false);
        textInputLayout.setHint(hint);
        textInputLayout.post(new Runnable() {
            @Override
            public void run() {
                textInputLayout.setHintAnimationEnabled(true);
            }
        });
    }
}
