import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;

public class ExamSection {
    public static void startExam(Connection connection, String username) {
        JFrame examFrame = new JFrame("Exam Section");
        examFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        examFrame.setSize(600, 400);
        examFrame.setLayout(new BorderLayout());

        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new GridLayout(5, 1));
        JLabel questionLabel = new JLabel("Question will appear here");
        JRadioButton option1 = new JRadioButton("Option 1");
        JRadioButton option2 = new JRadioButton("Option 2");
        JRadioButton option3 = new JRadioButton("Option 3");
        JRadioButton option4 = new JRadioButton("Option 4");
        ButtonGroup optionsGroup = new ButtonGroup();
        optionsGroup.add(option1);
        optionsGroup.add(option2);
        optionsGroup.add(option3);
        optionsGroup.add(option4);

        questionPanel.add(questionLabel);
        questionPanel.add(option1);
        questionPanel.add(option2);
        questionPanel.add(option3);
        questionPanel.add(option4);

        JButton nextButton = new JButton("Next");
        JButton submitButton = new JButton("Submit");
        submitButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nextButton);
        buttonPanel.add(submitButton);

        examFrame.add(questionPanel, BorderLayout.CENTER);
        examFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Load Questions from Database
        new Thread(() -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM questions");
                int[] questionCount = {0};
                int[] score = {0};

                while (rs.next()) {
                    questionCount[0]++;
                    String question = rs.getString("question");
                    String[] options = {
                            rs.getString("option1"),
                            rs.getString("option2"),
                            rs.getString("option3"),
                            rs.getString("option4")
                    };
                    int correctOption = rs.getInt("correct_option");

                    // Update UI with question and options
                    SwingUtilities.invokeAndWait(() -> {
                        questionLabel.setText("Q" + questionCount[0] + ": " + question);
                        option1.setText(options[0]);
                        option2.setText(options[1]);
                        option3.setText(options[2]);
                        option4.setText(options[3]);
                        optionsGroup.clearSelection();
                    });

                    // Wait for user to select an answer
                    nextButton.addActionListener(e -> {
                        int selectedOption = -1;
                        if (option1.isSelected()) selectedOption = 1;
                        else if (option2.isSelected()) selectedOption = 2;
                        else if (option3.isSelected()) selectedOption = 3;
                        else if (option4.isSelected()) selectedOption = 4;

                        if (selectedOption == correctOption) score[0]++;
                        synchronized (nextButton) {
                            nextButton.notify();
                        }
                    });

                    synchronized (nextButton) {
                        nextButton.wait();
                    }
                }

                // Display Final Score
                SwingUtilities.invokeAndWait(() -> {
                    JOptionPane.showMessageDialog(examFrame, "Exam Completed. Score: " + score[0] + "/" + questionCount[0]);
                    examFrame.dispose();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        submitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(examFrame, "Thank you for completing the exam!");
            examFrame.dispose();
        });

        examFrame.setVisible(true);
    }
}

