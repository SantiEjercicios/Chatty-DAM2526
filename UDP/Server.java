package UDP;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private DatagramSocket socket;

    private Set<InetSocketAddress> clientes = new HashSet<>();

    public Server(int puerto) throws SocketException {
        this.socket = new DatagramSocket(puerto);
    }

    public void iniciar() {
        System.out.println("Servidor UDP iniciado en el puerto 5000...");
        byte[] buffer = new byte[1024];

        try {
            while (true) {

                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(paqueteRecibido);

                String mensaje = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                InetSocketAddress direccionCliente = (InetSocketAddress) paqueteRecibido.getSocketAddress();


                if (!clientes.contains(direccionCliente)) {
                    clientes.add(direccionCliente);
                    System.out.println("Nuevo cliente conectado desde: " + direccionCliente);
                }

                System.out.println(mensaje);
                broadcast(mensaje, direccionCliente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) socket.close();
        }
    }

    private void broadcast(String mensaje, InetSocketAddress remitente) {
        byte[] data = mensaje.getBytes();
        for (InetSocketAddress cliente : clientes) {

            if (!cliente.equals(remitente)) {
                try {
                    DatagramPacket paqueteEnvio = new DatagramPacket(data, data.length, cliente.getAddress(), cliente.getPort());
                    socket.send(paqueteEnvio);
                } catch (IOException e) {
                    System.err.println("Error enviando a " + cliente);
                }
            }
        }
    }

    public static void main(String[] args) throws SocketException {
        Server servidor = new Server(5000);
        servidor.iniciar();
    }
}