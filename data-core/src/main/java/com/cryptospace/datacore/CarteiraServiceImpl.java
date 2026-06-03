package com.cryptospace.datacore;

import com.cryptospace.shared.CarteiraResponse;
import com.cryptospace.shared.CarteiraService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class CarteiraServiceImpl extends UnicastRemoteObject implements CarteiraService {
    private static final long serialVersionUID = 1L;

    private final CarteiraRepository repository;

    public CarteiraServiceImpl(CarteiraRepository repository) throws RemoteException {
        super();
        this.repository = repository;
    }

    @Override
    public CarteiraResponse buscarCarteiraByHash(String hash) throws RemoteException {
        try {
            System.out.println("Consulta recebida via RMI para o hash: " + hash);
            return repository.buscarPorHash(hash);
        } catch (SQLException exception) {
            throw new RemoteException("Erro ao consultar o banco MySQL no DATA-CORE", exception);
        }
    }
}
