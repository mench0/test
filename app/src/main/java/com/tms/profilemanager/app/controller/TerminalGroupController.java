package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.mapper.entity.TerminalGroupMapper;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.app.service.TerminalXlsFileService;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.create.TerminalGroupCreateDto;
import com.tms.profilemanager.dto.enums.sort.TerminalGroupSort;
import com.tms.profilemanager.dto.filter.TerminalGroupFilterDto;
import com.tms.profilemanager.dto.patch.TerminalGroupPatchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/terminal-group")
@Tag(name = "Terminal groups", description = "Группы терминалов")
public class TerminalGroupController extends AbstractCrudController<
        TerminalGroup, TerminalGroupDto, TerminalGroupCreateDto, TerminalGroupPatchDto, TerminalGroupFilterDto, TerminalGroupSort> {

    TerminalXlsFileService fileService;

    public TerminalGroupController(TerminalGroupService service, TerminalGroupMapper mapper,
                                   TerminalXlsFileService fileService) {
        super(service, mapper);
        this.fileService = fileService;
    }

    @GetMapping("/download-terminals/{id}")
    @Operation(summary = "Экспорт терминалов по id группы")
    public ResponseEntity<byte[]> downloadFileStream(@PathVariable("id") Long id) {
        byte[] bytes = fileService.downloadTerminals(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Terminals")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
                .body(bytes);
    }


}
