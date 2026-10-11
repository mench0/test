package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.mapper.entity.AcquirerMapper;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.create.AcquirerCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import com.tms.profilemanager.dto.enums.sort.AcquirerSort;
import com.tms.profilemanager.dto.filter.AcquirerFilterDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api/acquirers")
@Tag(name = "Acquirers", description = "Эквайеры")
public class AcquirerController extends
        AbstractCrudController<Acquirer, AcquirerDto, AcquirerCreateDto, AcquirerPatchDto, AcquirerFilterDto, AcquirerSort> {

    public AcquirerController(AcquirerService service, AcquirerMapper mapper) {
        super(service, mapper);
    }

    @GetMapping("/available-operations")
    @Operation(summary = "Получение списка всех доступных операций")
    public Set<AcquirerOperationType> getAllAcquirerOperations() {
        return Set.of(AcquirerOperationType.values());
    }

    @GetMapping("/iso-protocols")
    @Operation(summary = "Получение списка всех ISO протоколов")
    public Set<IsoProtocol.Dto> getAllIsoProtocols() {
        return Arrays.stream(IsoProtocol.values()).map(IsoProtocol.Dto::of).collect(toSet());
    }

    @GetMapping("/key-schemes")
    @Operation(summary = "Получение списка всех схем ключей")
    public Set<AcquirerKeyScheme> getAllKeySchemes() {
        return Set.of(AcquirerKeyScheme.values());
    }
}
