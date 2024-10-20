package com.task.Crm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class CrmSystemTests {
    private static final String BASE_URL_SELLERS = "/api/sellers";
    private static final String BASE_URL_TRANSACTIONS = "/api/transactions";

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {}

    @Test @DisplayName("Test CRUD operations for Seller")
    void testSellerEndpoints() throws Exception {
        testGetSellers();
        testGetSellerById();
        testGetSellerByIdNotFound();
        testPostCreateSeller();
        testPatchUpdateSeller();
        testGetTransactionBySellerId();
        testGetTransactionBySellerIdNotFound();
    }
    @Test @DisplayName("Test analyse operations for Seller")
    void testAnalyseSellerEndpoints() throws Exception {
        testGetBestSellerForPeriod();
        testGetBestSellerForPeriodBadRequest();
        testGetSellersBelowAmountForPeriod();
        testGetSellersBelowAmountForPeriodBadRequest();
    }

    @Test @DisplayName("Test CRUD operations for Transaction")
    void testTransactionEndpoints() throws Exception {
        testGetTransactions();
        testGetTransactionById();
        testGetTransactionByIdNotFound();
        testPostCreateTransaction();
    }

    private void testGetTransactionByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL_TRANSACTIONS + "/getTransaction/1000"))
                .andExpect(status().isNotFound());
    }

    private void testGetTransactionById() throws Exception {
        mockMvc.perform(get(BASE_URL_TRANSACTIONS + "/getTransaction/1"))
                .andExpect(status().isOk());
    }

    private void testGetTransactions() throws Exception {
        mockMvc.perform(get(BASE_URL_TRANSACTIONS + "/getTransactions"))
                .andExpect(status().isOk());
    }

    private void testPostCreateTransaction() throws Exception {
        mockMvc.perform(post(BASE_URL_TRANSACTIONS + "/createTransaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{    \n" +
                                "    \"amount\": 10,\n" +
                                "    \"paymentType\": \"CASH\",\n" +
                                "    \"seller\": {\n" +
                                "        \"id\": 1\n" +
                                "    }\n" +
                                "}"))
                .andExpect(status().isOk());

        String responseString = mockMvc.perform(get(BASE_URL_TRANSACTIONS + "/getMaxId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int currentTransactionId = Integer.parseInt(responseString);
        mockMvc.perform(delete(BASE_URL_TRANSACTIONS + "/deleteTransaction/" + currentTransactionId))
                .andExpect(status().isOk());
    }
    private void testGetSellersBelowAmountForPeriodBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getSellersBelowAmountForPeriod/500")
                        .param("startDate", (String) null)
                        .param("endDate", "2024-10-30T07:55:03"))
                .andExpect(status().isBadRequest());
    }

    private void testGetSellersBelowAmountForPeriod() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getSellersBelowAmountForPeriod/500")
                        .param("startDate", "2022-10-19T07:44:03")
                        .param("endDate", "2024-10-30T07:55:03"))
                .andExpect(status().isOk());
    }

    private void testGetBestSellerForPeriodBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getBestSellerForPeriod/incorrectPeriod")
                        .param("startDate", "2022-10-19T07:44:03")
                        .param("endDate", "2024-10-30T07:55:03"))
                .andExpect(status().isBadRequest());
    }

    private void testGetBestSellerForPeriod() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getBestSellerForPeriod/specifiedDates")
                        .param("startDate", "2022-10-19T07:44:03")
                        .param("endDate", "2024-10-30T07:55:03"))
                .andExpect(status().isOk());
    }

    private void testGetTransactionBySellerIdNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getTransactionBySellerId/1000"))
                .andExpect(status().isNotFound());
    }

    private void testGetTransactionBySellerId() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getTransactionBySellerId/1"))
                .andExpect(status().isOk());
    }

    private void testPostCreateSeller() throws Exception {
        mockMvc.perform(post(BASE_URL_SELLERS + "/createSeller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"test\",\n" +
                                "    \"contact_info\": \"test@company.com\"\n" +
                                "}"))
                .andExpect(status().isOk());

        String response = mockMvc.perform(get(BASE_URL_SELLERS + "/getSellerByName/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        int currentTestSellerId = jsonNode.get("id").asInt();
        mockMvc.perform(delete(BASE_URL_SELLERS + "/deleteSeller/" + currentTestSellerId));
    }

    private void testPatchUpdateSeller() throws Exception {
        mockMvc.perform(patch(BASE_URL_SELLERS + "/updateSeller/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Джордж Моа\"\n" +
                                "}"))
                .andExpect(status().isOk());
    }

    private void testGetSellerByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getSellerById/1000"))
                .andExpect(status().isNotFound());
    }

    private void testGetSellerById() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getSellerById/1"))
                .andExpect(status().isOk());
    }

    private void testGetSellers() throws Exception {
        mockMvc.perform(get(BASE_URL_SELLERS + "/getSellers"))
                .andExpect(status().isOk());
    }
}