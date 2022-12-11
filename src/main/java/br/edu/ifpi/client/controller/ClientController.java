package br.edu.ifpi.client.controller;

import br.edu.ifpi.client.domain.Client;
import br.edu.ifpi.client.domain.dto.ClientRequestBody;
import br.edu.ifpi.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    private final ClientService clientService;

    @GetMapping(path = "/client/pageable")
    public ResponseEntity<Page<Client>> findAll(Pageable pageable){
        Page<Client> clientPage = clientService.findAll(pageable);
        return ResponseEntity.ok(clientPage);
    }

    @GetMapping(path = "/client")
    public ResponseEntity<List<Client>> findAll(){
        List<Client> clientList = clientService.findAll();
        return ResponseEntity.ok(clientList);
    }

    @GetMapping(path = "/client/{id}")
    public ResponseEntity<Client> findById(@PathVariable Long id){
        Client clientFound = clientService.findById(id);
        return ResponseEntity.ok(clientFound);
    }

    @PostMapping(path = "/admin/client")
    public ResponseEntity<Client> save(@RequestBody @Valid ClientRequestBody clientRequestBody){
        Client clientToBeSaved = new Client();
        BeanUtils.copyProperties(clientRequestBody, clientToBeSaved);

        Client clientSaved = clientService.save(clientToBeSaved);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientSaved);
    }

    @PutMapping(path = "/admin/client/{id}")
    public ResponseEntity<Void> replace(@PathVariable Long id, @RequestBody @Valid ClientRequestBody clientRequestBody){
        Client clientToBeUpdated = new Client();
        BeanUtils.copyProperties(clientRequestBody, clientToBeUpdated);

        clientService.replace(id, clientToBeUpdated);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/admin/client/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
