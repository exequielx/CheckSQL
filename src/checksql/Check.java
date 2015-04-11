/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checksql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author odin
 */
public class Check {

    private JTextArea ta_resultado;
    private String query, ip, puerto, username, password, dbname, intervalo;
    Timer timer;
    int contador = 1;

    void setParameters(JTextArea ta_resultado, String query, String ip, String puerto, String username,
            String password, String dbname, String intervalo) {
        this.query = query;
        this.ip = ip;
        this.puerto = puerto;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
        this.intervalo = intervalo;
        this.ta_resultado = ta_resultado;
    }

    void start() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + ip + ":" + puerto + "/" + dbname,
                    username, password);

            timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        ta_resultado.setText("comprobando...(" + (contador++) + ")");
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            ta_resultado.setText("encontrado: " + rs.getString(1));
                            showAlert();
                            timer.cancel();
                        }
                        rs.close();
                        stmt.close();
                    } catch (Exception ex) {
                        ta_resultado.setText("ERROR: " + ex.getMessage());
                        timer.cancel();
                    }

                }
            };
            timer.schedule(task, 1, (Integer.parseInt(intervalo) * 1000));
        } catch (SQLException | NumberFormatException ex) {
            ta_resultado.setText("ERROR: " + ex.getMessage());
            timer.cancel();
        }
    }

    private void showAlert() {
        JOptionPane optionPane = new JOptionPane();
        JDialog dialog = optionPane.createDialog("Alerta");
        optionPane.setMessage("Un resultado ha sido encontrado!");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    void stop() {
        if (this.timer != null) {
            timer.cancel();
            ta_resultado.setText("stop");
        }

    }

}
