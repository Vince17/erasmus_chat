/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serveur;

/**
 *
 * @author damie
 */
public class ServeurInterface extends javax.swing.JFrame {
    //JAVA SPRING
    //TDD
    /**
     * Creates new form ServeurInterface
     */
    public ServeurInterface() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        DisplayMsg = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        DisplayMsg.setColumns(20);
        DisplayMsg.setRows(5);
        jScrollPane1.setViewportView(DisplayMsg);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //setup UI
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServeurInterface().setVisible(true);
            }
        });
        String host = "127.0.0.1";
        int port = 1201;
        
        System.out.println("préparation du serveur");
        ServerBack bs = new ServerBack(host, port);
        bs.open();
    }
    
    public static void PrintNewMessage(DataMessage msg){
        System.out.println("affichage du msg");
        DisplayMsg.setText(DisplayMsg.getText() + "\n " + msg.ToPrint());
    }
    
    public static void PrintLogin(String id){
        System.out.println("affichage du login");
        DisplayMsg.setText(DisplayMsg.getText() + "\n " + id + " join.");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextArea DisplayMsg;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
