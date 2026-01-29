package UDP;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    private DatagramSocket socket;
    private InetAddress direccionServidor;
    private int puertoServidor;
    private String nombreUsuario;

    public Cliente(String host, int puerto, String nombreUsuario) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.direccionServidor = InetAddress.getByName(host);
        this.puertoServidor = puerto;
        this.nombreUsuario = nombreUsuario;
    }

    public void enviarMensaje() {
        Scanner scanner = new Scanner(System.in);
        try {

            String inicio = "SERVER: " + nombreUsuario + " ha entrado al chat.";
            mandarPaquete(inicio);

            while (true) {
                String texto = scanner.nextLine();
                String mensajeFormateado = nombreUsuario + ": " + texto;
                mandarPaquete(mensajeFormateado);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mandarPaquete(String mensaje) throws IOException {
        byte[] buffer = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccionServidor, puertoServidor);
        socket.send(paquete);
    }

    public void escuchar() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                    socket.receive(paquete);
                    String recibido = new String(paquete.getData(), 0, paquete.getLength());
                    System.out.println(recibido);
                } catch (IOException e) {
                    System.err.println("Conexión perdida.");
                    break;
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce tu nombre: ");
        String nombre = sc.next();

        Cliente cliente = new Cliente("localhost", 5000, nombre);
        cliente.escuchar();
        cliente.enviarMensaje();
    }
}