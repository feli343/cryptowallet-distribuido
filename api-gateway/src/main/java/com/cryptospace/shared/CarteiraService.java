package com.cryptospace.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CarteiraService extends Remote {
    CarteiraResponse buscarCarteiraByHash(String hash) throws RemoteException;
}
