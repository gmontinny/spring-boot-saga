package br.com.saga.controller;

import br.com.saga.dto.request.OrderRequest;
import br.com.saga.dto.request.OrderUpdateRequest;
import br.com.saga.dto.response.OrderResponse;
import br.com.saga.saga.SagaOrchestrator;
import br.com.saga.service.OrderService;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gerenciamento de pedidos com padrão SAGA")
public class OrderController {

    private final OrderService orderService;
    private final SagaOrchestrator sagaOrchestrator;

    @GetMapping
    @Operation(summary = "Listar pedidos paginados", description = "Retorna todos os pedidos com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    public ResponseEntity<Page<OrderResponse>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido com itens e pagamentos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<OrderResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Buscar pedidos por cliente", description = "Retorna todos os pedidos de um cliente específico")
    @ApiResponse(responseCode = "200", description = "Pedidos do cliente retornados")
    public ResponseEntity<Page<OrderResponse>> findByCustomer(
            @PathVariable String customerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(orderService.findByCustomerId(customerId, pageable));
    }

    @PostMapping
    @Operation(summary = "Criar pedido via SAGA", description = "Cria um novo pedido orquestrado pelo padrão SAGA com eventos Kafka")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso via SAGA"),
            @ApiResponse(responseCode = "404", description = "Customer não encontrado"),
            @ApiResponse(responseCode = "422", description = "Falha na criação do pedido")
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sagaOrchestrator.createOrder(request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable String id,
                                                      @Valid @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover pedido", description = "Remove um pedido com seus itens e pagamentos")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
