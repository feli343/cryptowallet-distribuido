package com.cryptospace.datacore;

import com.cryptospace.shared.CarteiraService;

import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class DataCoreApplication {
    private static final int PORTA_RMI = 1099;
    private static final String SERVICE_NAME = "CarteiraService";

    public static void main(String[] args) throws Exception {
        criarRegistrySeNecessario();

        DatabaseConfig config = new DatabaseConfig();
        CarteiraRepository repository = new CarteiraRepository(config);
        CarteiraService service = new CarteiraServiceImpl(repository);

        Naming.rebind("rmi://localhost:" + PORTA_RMI + "/" + SERVICE_NAME, service);

        System.out.println("DATA-CORE iniciado com sucesso.");
        System.out.println("Servidor RMI registrado em rmi://localhost:" + PORTA_RMI + "/" + SERVICE_NAME);
        System.out.println("Banco configurado em: " + config.getUrl());
        System.out.println("Aguardando chamadas remotas da API Gateway...");
    }

    private static void criarRegistrySeNecessario() throws RemoteException, AlreadyBoundException {
        try {
            LocateRegistry.createRegistry(PORTA_RMI);
            System.out.println("Registry RMI criado na porta " + PORTA_RMI);
        } catch (RemoteException exception) {
            System.out.println("Registry RMI já estava ativo na porta " + PORTA_RMI);
        }
    }
}
