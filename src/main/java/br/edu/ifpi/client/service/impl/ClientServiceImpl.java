package br.edu.ifpi.client.service.impl;

import br.edu.ifpi.client.domain.Client;
import br.edu.ifpi.client.error.exception.ClientNotFoundException;
import br.edu.ifpi.client.repository.ClientRepository;
import br.edu.ifpi.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Page<Client> findAll(Pageable pageable) {
        log.info("Getting a client page");
        return clientRepository.findAll(pageable);
    }

    @Override
    public List<Client> findAll() {
        log.info("Getting all client");
        return clientRepository.findAll();
    }

    @Override
    public Client findById(Long id) {
        Client clientFound = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(String.format("Client ID %d not found!", id)));

        log.info("Getting the client: {}", clientFound);
        return clientFound;
    }

    @Override
    public Client save(Client client) {
        Client clientSaved = clientRepository.save(client);

        log.info("Client has been saved: {}", clientSaved);
        return clientSaved;
    }

    @Override
    public void replace(Long id, Client client) {
        Client clientToBeUpdated = findById(id);
        client.setId(clientToBeUpdated.getId());
        Client clientUpdated = clientRepository.save(client);

        log.info("Client has been updated: from {} to {}", clientToBeUpdated, clientUpdated);
    }

    @Override
    public void deleteById(Long id) {
        Client clientToBeDeleted = findById(id);
        clientRepository.delete(clientToBeDeleted);

        log.info("Client has been deleted: {}", clientToBeDeleted);
    }
}
