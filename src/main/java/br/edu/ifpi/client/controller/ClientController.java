package br.edu.ifpi.client.controller;

import br.edu.ifpi.client.domain.Client;
import br.edu.ifpi.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    private final ClientService clientService;

    @GetMapping(path = "/pageable") @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<Client>> findAll(Pageable pageable){
        Page<Client> clientPage = clientService.findAll(pageable);
        return ResponseEntity.ok(clientPage);
    }

    @GetMapping @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Client>> findAll(){
        List<Client> clientList = clientService.findAll();
        return ResponseEntity.ok(clientList);
    }

    @GetMapping(path = "/{id}") @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Client> findById(@PathVariable Long id){
        Client clientFound = clientService.findById(id);
        return ResponseEntity.ok(clientFound);
    }

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Client> save(@RequestBody Client client){
        Client clientSaved = clientService.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientSaved);
    }

    @PutMapping(path = "/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> replace(@PathVariable Long id, @RequestBody Client client){
        clientService.replace(id, client);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
