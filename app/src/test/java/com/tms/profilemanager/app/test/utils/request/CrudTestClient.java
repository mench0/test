package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.filter.BaseFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class CrudTestClient<D extends BaseDto, C extends BaseCreateDto<?>, P extends BasePatchDto<?>, F extends BaseFilterDto<?, F>> {

    Class<D> dtoClass;
    RequestSender requestSender;

    public D getById(Long id) {
        return requestSender.sendAndExpectOk(HttpMethod.GET, getBaseUrl() + "/" + id, null, dtoClass);
    }

    public void assertNotExists(Long id) {
        MvcResult mvcResult = requestSender.send(HttpMethod.GET, getBaseUrl() + "/" + id, null);
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    public Page<D> search(F filterDto) {
        SearchRequest<F, ?> searchRequest = SearchRequest.of(filterDto);
        return requestSender.sendAndExpectOk(HttpMethod.POST, getBaseUrl() + "/search", searchRequest, getPageDtoTypeReference());
    }

    public Optional<D> searchOne(F filterDto) {
        Page<D> result = search(filterDto);
        if (result.getSize() > 1) {
            throw new IllegalStateException("1 record expected, but " + result.getSize() + " found");
        }
        return result.get().findFirst();
    }

    protected abstract TypeReference<Page<D>> getPageDtoTypeReference();

    public D create(C createDto) {
        return requestSender.sendAndExpectOk(HttpMethod.POST, getBaseUrlToCreate(), createDto, dtoClass);
    }

    public D patch(P patchDto) {
        return requestSender.sendAndExpectOk(HttpMethod.PATCH, getBaseUrlToPatch(), patchDto, dtoClass);
    }

    public void deleteById(Long id) {
        requestSender.sendAndExpectOk(HttpMethod.DELETE, getBaseUrl() + "/" + id, null);
    }

    public void archiveById(Long id) {
        requestSender.sendAndExpectOk(HttpMethod.PUT, getBaseUrl() + "/" + id + "/archive", null);
    }

    public void restoreById(Long id) {
        requestSender.sendAndExpectOk(HttpMethod.PUT, getBaseUrl() + "/" + id + "/restore", null);
    }

    protected abstract String getBaseUrl();

    protected String getBaseUrlToCreate() {
        return getBaseUrl();
    }

    protected String getBaseUrlToPatch() {
        return getBaseUrl();
    }
}
