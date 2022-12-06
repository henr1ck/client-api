package br.edu.ifpi.client.service;

import br.edu.ifpi.client.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientService {
    Page<Client> findAll(Pageable pageable);
    List<Client> findAll();
    Client findById(Long id);
    Client save(Client client);
    void replace(Long id, Client client);
    void deleteById(Long id);
}
