package org.houxg.flexlayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.houxg.flexlayout.FlexLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private FlexLayout.Mode[] modes = new FlexLayout.Mode[]{
            FlexLayout.Mode.START,
            FlexLayout.Mode.END,
            FlexLayout.Mode.CENTER,
            FlexLayout.Mode.SPACE_BETWEEN,
            FlexLayout.Mode.SPACE_AROUND
    };

    private FlexLayout.Mode[] alignItemModes = new FlexLayout.Mode[]{
            FlexLayout.Mode.START,
            FlexLayout.Mode.END,
            FlexLayout.Mode.CENTER
    };

    @BindView(R.id.layout)
    FlexLayout mLayout;
    @BindView(R.id.ll_align_item)
    ViewGroup mAlignItemPanel;
    @BindView(R.id.ll_align_content)
    ViewGroup mAlignContentPanel;
    @BindView(R.id.ll_justify_content)
    ViewGroup mJustifyContentPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.houxg.flexlayoutdemo.R.layout.activity_main);
        ButterKnife.bind(this);

        mAlignItemPanel.addView(createRadioButtonGroups(alignItemModes, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mLayout.setAlignItem(alignItemModes[i]);
            }
        }));

        mAlignContentPanel.addView(createRadioButtonGroups(modes, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mLayout.setAlignContent(modes[i]);
            }
        }));

        mJustifyContentPanel.addView(createRadioButtonGroups(modes, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mLayout.setJustifyContent(modes[i]);
            }
        }));

    }

    private RadioGroup createRadioButtonGroups(FlexLayout.Mode[] modes, RadioGroup.OnCheckedChangeListener listener) {
        RadioButton[] radioButtons = new RadioButton[modes.length];
        RadioGroup group = new RadioGroup(this);
        group.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        group.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < radioButtons.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setTag(modes[i]);
            radioButton.setId(i);
            radioButton.setText(modes[i].name());
            radioButtons[i] = radioButton;
            group.addView(radioButton);
        }
        group.setOnCheckedChangeListener(listener);
        return group;
    }

}
