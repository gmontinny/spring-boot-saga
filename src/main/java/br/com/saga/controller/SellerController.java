package br.com.saga.controller;

import br.com.saga.dto.request.SellerRequest;
import br.com.saga.dto.response.SellerResponse;
import br.com.saga.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
@Tag(name = "Sellers", description = "CRUD completo de vendedores")
public class SellerController {

    private final SellerService sellerService;

    @GetMapping
    @Operation(summary = "Listar vendedores paginados", description = "Retorna todos os vendedores com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de vendedores retornada com sucesso")
    public ResponseEntity<Page<SellerResponse>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(sellerService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar vendedor por ID", description = "Retorna um vendedor pelo seu ID único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor encontrado"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado")
    })
    public ResponseEntity<SellerResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(sellerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo vendedor", description = "Cadastra um novo vendedor no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vendedor criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Vendedor já existe")
    })
    public ResponseEntity<SellerResponse> create(@Valid @RequestBody SellerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar vendedor", description = "Atualiza os dados de um vendedor existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado")
    })
    public ResponseEntity<SellerResponse> update(@PathVariable String id,
                                                 @Valid @RequestBody SellerRequest request) {
        return ResponseEntity.ok(sellerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover vendedor", description = "Remove um vendedor pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vendedor removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sellerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
