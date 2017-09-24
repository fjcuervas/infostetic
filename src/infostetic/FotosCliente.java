/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;


import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
//System.out.println("fichero. "+fichero);
/**
 *
 * @author Javi
 */
public class FotosCliente extends javax.swing.JDialog {

    private JFileChooser fileChooser;
    private Conexion consulta;
    String fecha; 

    public FotosCliente(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla
        consulta  = new Conexion();

        GregorianCalendar calendario=new GregorianCalendar();                
        //Hacer formateador de la fecha, se le pasa el formato en que se quiere obtener la fecha.
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd"); 
        fecha = dateformat.format(calendario.getTime());
        
        lbCliente.setText(SelCliente.nomCliente);
        //Creando FileChooser
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        //fileChooser.setCurrentDirectory(new java.io.File("C:\\Users\\Javi\\Pictures\\Infostetic"));
        
        accionesBotones();
         
        cargarFotos();
        
                         
    }
    
    private void cargarFotos() 
    {
        ResultSet rs;

        rs = consulta.selectResult("clientes", "*", " where id=" + SelCliente.idCliente);
        try 
        {
            while (rs.next())
            {
                try
                {
                    if (rs.getString("foto1").isEmpty()  == false)
                    {
                        ImageIcon ico1 = new ImageIcon(rs.getString("foto1"));
                        Icon icono1 = new ImageIcon(ico1.getImage().getScaledInstance(lblFoto1.getWidth(), 
                                                   lblFoto1.getHeight(), Image.SCALE_DEFAULT));

                        lblFoto1.setIcon(icono1);  
                        //configuro el título de la foto, extrayendo el final de la ruta del fichero
                        //que será el nombre de la imagen.
                        lbTituloFoto1.setText(rs.getString("foto1").substring(
                                rs.getString("foto1").lastIndexOf('\\') + 1, rs.getString("foto1").length()));
                        
                        lbFechaFoto1.setText(rs.getString("fecha_foto1"));
                        
                        repaint();
                    }
                }
                catch (NullPointerException npe)
                {
                    System.out.println("CADENA VACIA: ");
                }                
                try
                {
                    if (rs.getString("foto2").isEmpty() == false)
                    {                
                        ImageIcon ico2 = new ImageIcon(rs.getString("foto2"));
                        Icon icono2 = new ImageIcon(ico2.getImage().getScaledInstance(lblFoto2.getWidth(), 
                                                   lblFoto2.getHeight(), Image.SCALE_DEFAULT));
                        lblFoto2.setIcon(icono2); 
                        //configuro el título de la foto, extrayendo el final de la ruta del fichero
                        //que será el nombre de la imagen.
                        lbTituloFoto2.setText(rs.getString("foto2").substring(
                                rs.getString("foto2").lastIndexOf('\\') + 1, rs.getString("foto2").length()));
                        
                        lbFechaFoto2.setText(rs.getString("fecha_foto2"));                        
                        repaint();
                    }
                }
                catch (NullPointerException npe)
                {
                    System.out.println("CADENA VACIA: ");
                }                
                try
                {
                    if (rs.getString("foto3").isEmpty() == false)
                    {                
                        ImageIcon ico3 = new ImageIcon(rs.getString("foto3"));
                        Icon icono3 = new ImageIcon(ico3.getImage().getScaledInstance(lblFoto3.getWidth(), 
                                                   lblFoto3.getHeight(), Image.SCALE_DEFAULT));
                        lblFoto3.setIcon(icono3);          
                        //configuro el título de la foto, extrayendo el final de la ruta del fichero
                        //que será el nombre de la imagen.
                        lbTituloFoto3.setText(rs.getString("foto3").substring(
                                rs.getString("foto3").lastIndexOf('\\') + 1, rs.getString("foto3").length()));
                        
                        lbFechaFoto3.setText(rs.getString("fecha_foto3"));                        
                    }
                }
                catch (NullPointerException npe)
                {
                    System.out.println("CADENA VACIA: ");
                }                
                try
                {
                    if (rs.getString("foto4").isEmpty() == false)
                    {                
                        ImageIcon ico4 = new ImageIcon(rs.getString("foto4"));
                        Icon icono4 = new ImageIcon(ico4.getImage().getScaledInstance(lblFoto4.getWidth(), 
                                                   lblFoto4.getHeight(), Image.SCALE_DEFAULT));
                        lblFoto4.setIcon(icono4); 
                        //configuro el título de la foto, extrayendo el final de la ruta del fichero
                        //que será el nombre de la imagen.
                        lbTituloFoto4.setText(rs.getString("foto4").substring(
                                rs.getString("foto4").lastIndexOf('\\') + 1, rs.getString("foto4").length()));
                        
                        lbFechaFoto4.setText(rs.getString("fecha_foto4"));                        
                    }
                }
                catch (NullPointerException npe)
                {
                    System.out.println("CADENA VACIA: ");
                }                
                try
                {
                    if (rs.getString("foto5").isEmpty() == false)
                    {                
                        ImageIcon ico5 = new ImageIcon(rs.getString("foto5"));
                        Icon icono5 = new ImageIcon(ico5.getImage().getScaledInstance(lblFoto5.getWidth(), 
                                                   lblFoto5.getHeight(), Image.SCALE_DEFAULT));
                        
                        lblFoto5.setIcon(icono5);               
                        //configuro el título de la foto, extrayendo el final de la ruta del fichero
                        //que será el nombre de la imagen.
                        lbTituloFoto5.setText(rs.getString("foto5").substring(
                                rs.getString("foto5").lastIndexOf('\\') + 1, rs.getString("foto5").length()));
                        
                        lbFechaFoto5.setText(rs.getString("fecha_foto5"));                        
                    }
                }
                catch (NullPointerException npe)
                {
                    System.out.println("CADENA VACIA: ");
                }                
                try
                {                
                    if (rs.getString("foto6").isEmpty() == false)
                    {                
                        ImageIcon ico6 = new ImageIcon(rs.getString("foto6"));
                        Icon icono6 = new ImageIcon(ico6.getImage().getScaledInstance(lblFoto6.getWidth(), 
                                                   lblFoto6.getHeight(), Image.SCALE_DEFAULT));
                        
                        lblFoto6.setIcon(icono6); 
                        //configuro el título de la foto, extrayendo el final de la ruta del fichero
                        //que será el nombre de la imagen.
                        lbTituloFoto6.setText(rs.getString("foto6").substring(
                                rs.getString("foto6").lastIndexOf('\\') + 1, rs.getString("foto6").length()));
                        
                        lbFechaFoto6.setText(rs.getString("fecha_foto6"));                        
                    }
                }
                catch (NullPointerException npe)
                {
                    System.out.println("CADENA VACIA: ");
                }                                                       
            }
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        
    }
    
    private void accionesBotones() 
    {
        
        btnBuscar1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Accion del fileChooser
                if (fileChooser.showDialog(null, "Abrir imagen") == JFileChooser.APPROVE_OPTION) 
                {
                    String fichero = fileChooser.getSelectedFile().toString().replace("\\" , "\\\\");;
                    ImageIcon ico = new ImageIcon(fichero);
                    Icon icono = new ImageIcon(ico.getImage().getScaledInstance(lblFoto1.getWidth(), 
                                                              lblFoto1.getHeight(), Image.SCALE_DEFAULT));
                    
                    lbTituloFoto1.setText(fileChooser.getName(fileChooser.getSelectedFile()));
                    lbFechaFoto1.setText(fecha);
                    lblFoto1.setIcon(icono);                    
                    repaint();
                    
                    consulta.update("clientes", "foto1='" + fichero + "', fecha_foto1='" + fecha + "'", 
                            "id=" + SelCliente.idCliente);
                }
                        
            }
        });        
                                            
        btnBuscar2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Accion del fileChooser
                if (fileChooser.showDialog(null, "Abrir imagen") == JFileChooser.APPROVE_OPTION) 
                {
                    String fichero = fileChooser.getSelectedFile().toString().replace("\\" , "\\\\");;
                    ImageIcon ico = new ImageIcon(fichero);
                    Icon icono = new ImageIcon(ico.getImage().getScaledInstance(lblFoto2.getWidth(), 
                                                              lblFoto2.getHeight(), Image.SCALE_DEFAULT));
                    
                    lbTituloFoto2.setText(fileChooser.getName(fileChooser.getSelectedFile()));
                    lbFechaFoto2.setText(fecha);                    
                    lblFoto2.setIcon(icono);                    
                    repaint();
                    
                    consulta.update("clientes", "foto2='" + fichero + "', fecha_foto2='" + fecha + "'", 
                            "id=" + SelCliente.idCliente);
                }
                        
            }
        });  
        
        btnBuscar3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Accion del fileChooser
                if (fileChooser.showDialog(null, "Abrir imagen") == JFileChooser.APPROVE_OPTION) 
                {
                    String fichero = fileChooser.getSelectedFile().toString().replace("\\" , "\\\\");;
                    ImageIcon ico = new ImageIcon(fichero);
                    Icon icono = new ImageIcon(ico.getImage().getScaledInstance(lblFoto3.getWidth(), 
                                                              lblFoto3.getHeight(), Image.SCALE_DEFAULT));
                    
                    lbTituloFoto3.setText(fileChooser.getName(fileChooser.getSelectedFile()));
                    lbFechaFoto3.setText(fecha);                    
                    lblFoto3.setIcon(icono);                    
                    repaint();
                    
                    consulta.update("clientes", "foto3='" + fichero + "', fecha_foto3='" + fecha + "'", 
                            "id=" + SelCliente.idCliente);
                }
                        
            }
        });       
        
        btnBuscar4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Accion del fileChooser
                if (fileChooser.showDialog(null, "Abrir imagen") == JFileChooser.APPROVE_OPTION) 
                {
                    String fichero = fileChooser.getSelectedFile().toString().replace("\\" , "\\\\");;
                    ImageIcon ico = new ImageIcon(fichero);
                    Icon icono = new ImageIcon(ico.getImage().getScaledInstance(lblFoto4.getWidth(), 
                                                              lblFoto4.getHeight(), Image.SCALE_DEFAULT));
                    
                    lbTituloFoto4.setText(fileChooser.getName(fileChooser.getSelectedFile()));
                    lbFechaFoto4.setText(fecha);                    
                    lblFoto4.setIcon(icono);                    
                    repaint();
                    
                    consulta.update("clientes", "foto4='" + fichero + "', fecha_foto4='" + fecha + "'", 
                            "id=" + SelCliente.idCliente);
                }
                        
            }
        });        
                                            
        btnBuscar5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Accion del fileChooser
                if (fileChooser.showDialog(null, "Abrir imagen") == JFileChooser.APPROVE_OPTION) 
                {
                    String fichero = fileChooser.getSelectedFile().toString().replace("\\" , "\\\\");;
                    ImageIcon ico = new ImageIcon(fichero);
                    Icon icono = new ImageIcon(ico.getImage().getScaledInstance(lblFoto5.getWidth(), 
                                                              lblFoto5.getHeight(), Image.SCALE_DEFAULT));
                    
                    lbTituloFoto5.setText(fileChooser.getName(fileChooser.getSelectedFile()));
                    lbFechaFoto5.setText(fecha);                      
                    lblFoto5.setIcon(icono);                    
                    repaint();
                    
                    consulta.update("clientes", "foto5='" + fichero + "', fecha_foto5='" + fecha + "'", 
                            "id=" + SelCliente.idCliente);
                }
                        
            }
        });        
                                            
        btnBuscar6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Accion del fileChooser
                if (fileChooser.showDialog(null, "Abrir imagen") == JFileChooser.APPROVE_OPTION) 
                {
                    String fichero = fileChooser.getSelectedFile().toString().replace("\\" , "\\\\");;
                    ImageIcon ico = new ImageIcon(fichero);
                    Icon icono = new ImageIcon(ico.getImage().getScaledInstance(lblFoto6.getWidth(), 
                                                              lblFoto6.getHeight(), Image.SCALE_DEFAULT));
                    
                    lbTituloFoto6.setText(fileChooser.getName(fileChooser.getSelectedFile()));
                    lbFechaFoto6.setText(fecha);                      
                    lblFoto6.setIcon(icono);                    
                    repaint();
                    
                    consulta.update("clientes", "foto6='" + fichero + "', fecha_foto6='" + fecha + "'", 
                            "id=" + SelCliente.idCliente);
                }
                        
            }
        });                        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbCliente = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        panelFoto1 = new javax.swing.JPanel();
        lbFechaFoto1 = new javax.swing.JLabel();
        lblFoto1 = new javax.swing.JLabel();
        btnBuscar1 = new javax.swing.JButton();
        btnEliminar1 = new javax.swing.JButton();
        lbTituloFoto1 = new javax.swing.JLabel();
        panelFoto2 = new javax.swing.JPanel();
        lbFechaFoto2 = new javax.swing.JLabel();
        lblFoto2 = new javax.swing.JLabel();
        btnBuscar2 = new javax.swing.JButton();
        btnEliminar2 = new javax.swing.JButton();
        lbTituloFoto2 = new javax.swing.JLabel();
        panelFoto3 = new javax.swing.JPanel();
        lbFechaFoto3 = new javax.swing.JLabel();
        lblFoto3 = new javax.swing.JLabel();
        btnBuscar3 = new javax.swing.JButton();
        btnEliminar3 = new javax.swing.JButton();
        lbTituloFoto3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        panelFoto6 = new javax.swing.JPanel();
        lbFechaFoto6 = new javax.swing.JLabel();
        lblFoto6 = new javax.swing.JLabel();
        btnBuscar6 = new javax.swing.JButton();
        btnEliminar6 = new javax.swing.JButton();
        lbTituloFoto6 = new javax.swing.JLabel();
        panelFoto5 = new javax.swing.JPanel();
        lbFechaFoto5 = new javax.swing.JLabel();
        lblFoto5 = new javax.swing.JLabel();
        btnBuscar5 = new javax.swing.JButton();
        btnEliminar5 = new javax.swing.JButton();
        lbTituloFoto5 = new javax.swing.JLabel();
        panelFoto4 = new javax.swing.JPanel();
        lbFechaFoto4 = new javax.swing.JLabel();
        lblFoto4 = new javax.swing.JLabel();
        lbTituloFoto4 = new javax.swing.JLabel();
        btnBuscar4 = new javax.swing.JButton();
        btnEliminar4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(990, 746));
        setResizable(false);

        lbCliente.setFont(new java.awt.Font("Cambria", 0, 30)); // NOI18N
        lbCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbCliente.setText("Cliente");
        lbCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jPanel1.setPreferredSize(new java.awt.Dimension(960, 387));

        panelFoto1.setPreferredSize(new java.awt.Dimension(248, 320));

        lbFechaFoto1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFechaFoto1.setText("Fecha Foto1");
        lbFechaFoto1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblFoto1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFoto1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        lblFoto1.setToolTipText("");
        lblFoto1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnBuscar1.setText("Buscar foto");
        btnBuscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar1ActionPerformed(evt);
            }
        });

        btnEliminar1.setText("Eliminar foto");
        btnEliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar1ActionPerformed(evt);
            }
        });

        lbTituloFoto1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTituloFoto1.setText("Titulo Foto1");
        lbTituloFoto1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout panelFoto1Layout = new javax.swing.GroupLayout(panelFoto1);
        panelFoto1.setLayout(panelFoto1Layout);
        panelFoto1Layout.setHorizontalGroup(
            panelFoto1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFoto1Layout.createSequentialGroup()
                .addComponent(lblFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelFoto1Layout.createSequentialGroup()
                .addGroup(panelFoto1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFoto1Layout.createSequentialGroup()
                        .addComponent(btnBuscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbFechaFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbTituloFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelFoto1Layout.setVerticalGroup(
            panelFoto1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFoto1Layout.createSequentialGroup()
                .addComponent(lbTituloFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(panelFoto1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscar1)
                    .addComponent(lbFechaFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar1))
                .addGap(0, 0, 0))
        );

        panelFoto2.setPreferredSize(new java.awt.Dimension(248, 320));

        lbFechaFoto2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFechaFoto2.setText("Fecha Foto2");
        lbFechaFoto2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblFoto2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFoto2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        lblFoto2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnBuscar2.setText("Buscar foto");
        btnBuscar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar2ActionPerformed(evt);
            }
        });

        btnEliminar2.setText("Eliminar foto");
        btnEliminar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar2ActionPerformed(evt);
            }
        });

        lbTituloFoto2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTituloFoto2.setText("Titulo Foto2");
        lbTituloFoto2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout panelFoto2Layout = new javax.swing.GroupLayout(panelFoto2);
        panelFoto2.setLayout(panelFoto2Layout);
        panelFoto2Layout.setHorizontalGroup(
            panelFoto2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbTituloFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFoto2Layout.createSequentialGroup()
                .addComponent(btnBuscar2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbFechaFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelFoto2Layout.setVerticalGroup(
            panelFoto2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFoto2Layout.createSequentialGroup()
                .addComponent(lbTituloFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(panelFoto2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscar2)
                    .addComponent(lbFechaFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar2)))
        );

        panelFoto3.setPreferredSize(new java.awt.Dimension(248, 320));

        lbFechaFoto3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFechaFoto3.setText("Fecha Foto3");
        lbFechaFoto3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblFoto3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFoto3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        lblFoto3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnBuscar3.setText("Buscar foto");
        btnBuscar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar3ActionPerformed(evt);
            }
        });

        btnEliminar3.setText("Eliminar foto");
        btnEliminar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar3ActionPerformed(evt);
            }
        });

        lbTituloFoto3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTituloFoto3.setText("Titulo Foto3");
        lbTituloFoto3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout panelFoto3Layout = new javax.swing.GroupLayout(panelFoto3);
        panelFoto3.setLayout(panelFoto3Layout);
        panelFoto3Layout.setHorizontalGroup(
            panelFoto3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFoto3Layout.createSequentialGroup()
                .addComponent(btnBuscar3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFechaFoto3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblFoto3, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lbTituloFoto3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelFoto3Layout.setVerticalGroup(
            panelFoto3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFoto3Layout.createSequentialGroup()
                .addComponent(lbTituloFoto3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFoto3, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(panelFoto3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscar3)
                    .addComponent(lbFechaFoto3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar3)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(panelFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFoto3, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(panelFoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(panelFoto3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(970, 387));

        panelFoto6.setPreferredSize(new java.awt.Dimension(248, 320));

        lbFechaFoto6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFechaFoto6.setText("Fecha Foto6");
        lbFechaFoto6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblFoto6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFoto6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        lblFoto6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnBuscar6.setText("Buscar foto");
        btnBuscar6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar6ActionPerformed(evt);
            }
        });

        btnEliminar6.setText("Eliminar foto");
        btnEliminar6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar6ActionPerformed(evt);
            }
        });

        lbTituloFoto6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTituloFoto6.setText("Titulo Foto6");
        lbTituloFoto6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout panelFoto6Layout = new javax.swing.GroupLayout(panelFoto6);
        panelFoto6.setLayout(panelFoto6Layout);
        panelFoto6Layout.setHorizontalGroup(
            panelFoto6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFoto6Layout.createSequentialGroup()
                .addGroup(panelFoto6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFoto6Layout.createSequentialGroup()
                        .addComponent(btnBuscar6, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbFechaFoto6, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbTituloFoto6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFoto6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblFoto6, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelFoto6Layout.setVerticalGroup(
            panelFoto6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFoto6Layout.createSequentialGroup()
                .addComponent(lbTituloFoto6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFoto6, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(panelFoto6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFechaFoto6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar6)
                    .addComponent(btnEliminar6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFoto5.setPreferredSize(new java.awt.Dimension(248, 320));

        lbFechaFoto5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFechaFoto5.setText("Fecha Foto5");
        lbFechaFoto5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblFoto5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFoto5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        lblFoto5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnBuscar5.setText("Buscar foto");
        btnBuscar5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar5ActionPerformed(evt);
            }
        });

        btnEliminar5.setText("Eliminar foto");
        btnEliminar5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar5ActionPerformed(evt);
            }
        });

        lbTituloFoto5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTituloFoto5.setText("Titulo Foto5");
        lbTituloFoto5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout panelFoto5Layout = new javax.swing.GroupLayout(panelFoto5);
        panelFoto5.setLayout(panelFoto5Layout);
        panelFoto5Layout.setHorizontalGroup(
            panelFoto5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbTituloFoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFoto5Layout.createSequentialGroup()
                .addComponent(btnBuscar5, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbFechaFoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar5, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblFoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelFoto5Layout.setVerticalGroup(
            panelFoto5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFoto5Layout.createSequentialGroup()
                .addComponent(lbTituloFoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(panelFoto5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbFechaFoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar5)
                    .addComponent(btnEliminar5)))
        );

        panelFoto4.setPreferredSize(new java.awt.Dimension(248, 320));

        lbFechaFoto4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFechaFoto4.setText("Fecha Foto4");
        lbFechaFoto4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblFoto4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFoto4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        lblFoto4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lbTituloFoto4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTituloFoto4.setText("Titulo Foto4");
        lbTituloFoto4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnBuscar4.setText("Buscar foto");
        btnBuscar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar4ActionPerformed(evt);
            }
        });

        btnEliminar4.setText("Eliminar foto");
        btnEliminar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFoto4Layout = new javax.swing.GroupLayout(panelFoto4);
        panelFoto4.setLayout(panelFoto4Layout);
        panelFoto4Layout.setHorizontalGroup(
            panelFoto4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbTituloFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panelFoto4Layout.createSequentialGroup()
                .addComponent(btnBuscar4, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFechaFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelFoto4Layout.setVerticalGroup(
            panelFoto4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFoto4Layout.createSequentialGroup()
                .addComponent(lbTituloFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(panelFoto4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFechaFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar4)
                    .addComponent(btnEliminar4)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(panelFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFoto6, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelFoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelFoto5, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                    .addComponent(panelFoto6, javax.swing.GroupLayout.PREFERRED_SIZE, 333, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 963, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lbCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 963, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void btnBuscar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar3ActionPerformed

        
    }//GEN-LAST:event_btnBuscar3ActionPerformed

    private void btnEliminar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar3ActionPerformed

        consulta.update("clientes", "foto3=" + null + ", fecha_foto3=" + null, "id=" + SelCliente.idCliente);
        
        String path = "/imagenes/cancelar.png";  
        URL url = this.getClass().getResource(path);  
        ImageIcon icon = new ImageIcon(url);     
        
        lblFoto3.setIcon(icon);             
        lbFechaFoto3.setText("Fecha Foto3");
        lbTituloFoto3.setText("Titulo Foto3");        
        repaint();
    }//GEN-LAST:event_btnEliminar3ActionPerformed

    private void btnBuscar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscar1ActionPerformed

    private void btnEliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar1ActionPerformed

        consulta.update("clientes", "foto1=" + null + ", fecha_foto1=" + null, "id=" + SelCliente.idCliente);        

        String path = "/imagenes/cancelar.png";  
        URL url = this.getClass().getResource(path);  
        ImageIcon icon = new ImageIcon(url);          
        
        lblFoto1.setIcon(icon); 
        lbFechaFoto1.setText("Fecha Foto1");
        lbTituloFoto1.setText("Titulo Foto1");
        repaint();
        
        
    }//GEN-LAST:event_btnEliminar1ActionPerformed

    private void btnBuscar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar2ActionPerformed
    
    }//GEN-LAST:event_btnBuscar2ActionPerformed

    private void btnEliminar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar2ActionPerformed

        consulta.update("clientes", "foto2=" + null + ", fecha_foto2=" + null, "id=" + SelCliente.idCliente);
        
        String path = "/imagenes/cancelar.png";  
        URL url = this.getClass().getResource(path);  
        ImageIcon icon = new ImageIcon(url);       
        
        lblFoto2.setIcon(icon); 
        lbFechaFoto2.setText("Fecha Foto2");
        lbTituloFoto3.setText("Titulo Foto2");        
        repaint();
    }//GEN-LAST:event_btnEliminar2ActionPerformed

    private void btnBuscar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscar4ActionPerformed

    private void btnEliminar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar4ActionPerformed

        consulta.update("clientes", "foto4=" + null + ", fecha_foto4=" + null, "id=" + SelCliente.idCliente);
        
        String path = "/imagenes/cancelar.png";  
        URL url = this.getClass().getResource(path);  
        ImageIcon icon = new ImageIcon(url); 
        
        lblFoto4.setIcon(icon);       
        lbFechaFoto4.setText("Fecha Foto4");
        lbTituloFoto4.setText("Titulo Foto4");        
        repaint();        
    }//GEN-LAST:event_btnEliminar4ActionPerformed

    private void btnBuscar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscar5ActionPerformed

    private void btnEliminar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar5ActionPerformed

        consulta.update("clientes", "foto5=" + null + ", fecha_foto5=" + null, "id=" + SelCliente.idCliente);
        
        String path = "/imagenes/cancelar.png";  
        URL url = this.getClass().getResource(path);  
        ImageIcon icon = new ImageIcon(url);     
        
        lblFoto5.setIcon(icon);             
        lbFechaFoto5.setText("Fecha Foto5");
        lbTituloFoto5.setText("Titulo Foto5");        
        repaint();    
    }//GEN-LAST:event_btnEliminar5ActionPerformed

    private void btnBuscar6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscar6ActionPerformed

    private void btnEliminar6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar6ActionPerformed
        
        consulta.update("clientes", "foto6=" + null + ", fecha_foto6=" + null, "id=" + SelCliente.idCliente);
        
        String path = "/imagenes/cancelar.png";  
        URL url = this.getClass().getResource(path);  
        ImageIcon icon = new ImageIcon(url);     
        
        lblFoto6.setIcon(icon);             
        lbFechaFoto6.setText("Fecha Foto6");
        lbTituloFoto6.setText("Titulo Foto6");        
        repaint();          
    }//GEN-LAST:event_btnEliminar6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FotosCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FotosCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FotosCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FotosCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FotosCliente dialog = new FotosCliente(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar1;
    private javax.swing.JButton btnBuscar2;
    private javax.swing.JButton btnBuscar3;
    private javax.swing.JButton btnBuscar4;
    private javax.swing.JButton btnBuscar5;
    private javax.swing.JButton btnBuscar6;
    private javax.swing.JButton btnEliminar1;
    private javax.swing.JButton btnEliminar2;
    private javax.swing.JButton btnEliminar3;
    private javax.swing.JButton btnEliminar4;
    private javax.swing.JButton btnEliminar5;
    private javax.swing.JButton btnEliminar6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lbCliente;
    private javax.swing.JLabel lbFechaFoto1;
    private javax.swing.JLabel lbFechaFoto2;
    private javax.swing.JLabel lbFechaFoto3;
    private javax.swing.JLabel lbFechaFoto4;
    private javax.swing.JLabel lbFechaFoto5;
    private javax.swing.JLabel lbFechaFoto6;
    private javax.swing.JLabel lbTituloFoto1;
    private javax.swing.JLabel lbTituloFoto2;
    private javax.swing.JLabel lbTituloFoto3;
    private javax.swing.JLabel lbTituloFoto4;
    private javax.swing.JLabel lbTituloFoto5;
    private javax.swing.JLabel lbTituloFoto6;
    private javax.swing.JLabel lblFoto1;
    private javax.swing.JLabel lblFoto2;
    private javax.swing.JLabel lblFoto3;
    private javax.swing.JLabel lblFoto4;
    private javax.swing.JLabel lblFoto5;
    private javax.swing.JLabel lblFoto6;
    private javax.swing.JPanel panelFoto1;
    private javax.swing.JPanel panelFoto2;
    private javax.swing.JPanel panelFoto3;
    private javax.swing.JPanel panelFoto4;
    private javax.swing.JPanel panelFoto5;
    private javax.swing.JPanel panelFoto6;
    // End of variables declaration//GEN-END:variables




}
