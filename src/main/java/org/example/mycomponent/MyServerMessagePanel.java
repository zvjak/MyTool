package org.example.mycomponent;

import org.example.util.CommonUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyServerMessagePanel extends JPanel {
    public static final int DefaultWidth = 600;
    public static final int DefaultHeight = 120;
    private final JComboBox<String> box = new JComboBox<>(new String[]{"nj-bigdata-warehouse01.u4a.cn","nj-bigdata-warehouse02.u4a.cn","nj-bigdata-warehouse03.u4a.cn"});
    private final JLabel[] labels = new JLabel[2];
    private final JTextField[] textFields = new JTextField[2];
    private final String[] labelNames = new String[]{"文件保存路径:", "文件名(默认同名):"};
    private final int defaultTextFieldLength = 24;

    public List<String> getTextFieldValues() {
        List<String> list = new ArrayList<>();
        String host = (String) box.getSelectedItem();
        list.add(host);
        for (int i = 0; i < textFields.length; i++) {
            list.add(textFields[i].getText());
        }
        return list;
    }

    public MyServerMessagePanel(MyJFrame jFrame) {
        this(jFrame, DefaultWidth, DefaultHeight);
    }

    public MyServerMessagePanel(MyJFrame jFrame, int width, int height) {
        this.setLayout(new GridBagLayout());
        this.setSize(width, height);
        this.setBounds(jFrame.getSize().width - this.getWidth(), 0, width, height);
        fillLabelAndTextField();
        jFrame.add(this);
        jFrame.setMyServerMessagePanel(this);
    }

    private void fillLabelAndTextField() {
        List<String> strings = CommonUtil.readFormFile();
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel(labelNames[i]);
            textFields[i] = new JTextField(defaultTextFieldLength);
            textFields[i].setText(strings.get(i));
        }
        setLabelTextPos();
    }

    private void setLabelTextPos() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);
        int[] dx = new int[]{0, 1};
        int[] dy = new int[]{0, 1, 2};
        constraints.gridx = 1;
        constraints.gridy = 0;
        this.add(box, constraints);
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                constraints.gridx = dx[j];
                constraints.gridy = dy[i];
                if (j == 0) {
                    this.add(labels[i - 1], constraints);
                } else {
                    this.add(textFields[i - 1], constraints);
                }
            }
        }
    }
}
