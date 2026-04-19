//package com.frauddetection;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.frauddetection.dto.ReviewRequest;
//import com.frauddetection.dto.TransactionRequest;
//import com.frauddetection.model.ReviewOutcome;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class FraudDetectionIntegrationTest {
//
//    @Autowired MockMvc mockMvc;
//    @Autowired ObjectMapper objectMapper;
//
//    @Test
//    @DisplayName("Normal low-risk transaction should be COMPLETED")
//    void normalTransactionShouldBeApproved() throws Exception {
//        TransactionRequest req = TransactionRequest.builder()
//            .accountId("acc-001")
//            .amount(new BigDecimal("50.00"))
//            .currency("EUR")
//            .merchant("Albert Heijn")
//            .merchantCategory("GROCERY")
//            .locationCountry("NL")
//            .locationCity("Amsterdam")
//            .build();
//
//        mockMvc.perform(post("/api/v1/fraud/analyze")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.status").value("COMPLETED"))
//            .andExpect(jsonPath("$.flagged").value(false))
//            .andExpect(jsonPath("$.riskScore").value(lessThan(50)));
//    }
//
//    @Test
//    @DisplayName("Large transfer from new location should be FLAGGED or BLOCKED")
//    void largeTransferFromNewLocationShouldBeFlagged() throws Exception {
//        TransactionRequest req = TransactionRequest.builder()
//            .accountId("acc-001")
//            .amount(new BigDecimal("15000.00"))
//            .currency("EUR")
//            .merchant("Unknown Wire Transfer")
//            .merchantCategory("TRANSFER")
//            .locationCountry("RU") // New location for acc-001
//            .locationCity("Moscow")
//            .build();
//
//        mockMvc.perform(post("/api/v1/fraud/analyze")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)))
//            .andExpect(jsonPath("$.flagged").value(true))
//            .andExpect(jsonPath("$.riskScore").value(greaterThanOrEqualTo(50)));
//    }
//
//    @Test
//    @DisplayName("Transaction from blacklisted country should be BLOCKED")
//    void transactionFromBlacklistedCountryShouldBeBlocked() throws Exception {
//        TransactionRequest req = TransactionRequest.builder()
//            .accountId("acc-001")
//            .amount(new BigDecimal("100.00"))
//            .currency("EUR")
//            .merchant("Unknown Merchant")
//            .merchantCategory("OTHER")
//            .locationCountry("NG") // Nigeria — blacklisted
//            .locationCity("Lagos")
//            .build();
//
//        mockMvc.perform(post("/api/v1/fraud/analyze")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)))
//            .andExpect(status().isForbidden())
//            .andExpect(jsonPath("$.status").value("BLOCKED"))
//            .andExpect(jsonPath("$.flaggedReasons", hasItem("BLACKLISTED_COUNTRY")));
//    }
//
//    @Test
//    @DisplayName("Blacklisted account transaction should be BLOCKED immediately")
//    void blacklistedAccountShouldBeBlocked() throws Exception {
//        TransactionRequest req = TransactionRequest.builder()
//            .accountId("acc-004") // blacklisted account
//            .amount(new BigDecimal("10.00"))
//            .currency("EUR")
//            .merchant("Any Merchant")
//            .merchantCategory("RETAIL")
//            .locationCountry("NL")
//            .locationCity("Amsterdam")
//            .build();
//
//        mockMvc.perform(post("/api/v1/fraud/analyze")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)))
//            .andExpect(status().isForbidden())
//            .andExpect(jsonPath("$.status").value("BLOCKED"))
//            .andExpect(jsonPath("$.flaggedReasons", hasItem("BLACKLISTED_ACCOUNT")));
//    }
//
//    @Test
//    @DisplayName("Unusual hour transaction should add risk score")
//    void unusualHourTransactionShouldAddRisk() throws Exception {
//        TransactionRequest req = TransactionRequest.builder()
//            .accountId("acc-001")
//            .amount(new BigDecimal("200.00"))
//            .currency("EUR")
//            .merchant("Night Club")
//            .merchantCategory("ENTERTAINMENT")
//            .locationCountry("NL")
//            .locationCity("Amsterdam")
//            .transactionTime(LocalDateTime.now().withHour(3)) // 3 AM
//            .build();
//
//        mockMvc.perform(post("/api/v1/fraud/analyze")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.riskScore").value(greaterThan(0)));
//    }
//
//    @Test
//    @DisplayName("Manual review workflow - flag then mark as false positive")
//    void manualReviewWorkflow() throws Exception {
//        // Step 1: Create a flagged transaction
//        TransactionRequest req = TransactionRequest.builder()
//            .accountId("acc-001")
//            .amount(new BigDecimal("12000.00"))
//            .currency("EUR")
//            .merchant("Wire Transfer")
//            .merchantCategory("TRANSFER")
//            .locationCountry("US")
//            .locationCity("New York")
//            .build();
//
//        MvcResult result = mockMvc.perform(post("/api/v1/fraud/analyze")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)))
//            .andReturn();
//
//        String responseBody = result.getResponse().getContentAsString();
//        String transactionId = objectMapper.readTree(responseBody).get("id").asText();
//
//        // Step 2: Submit manual review as false positive
//        ReviewRequest review = ReviewRequest.builder()
//            .outcome(ReviewOutcome.FALSE_POSITIVE)
//            .reviewedBy("analyst-001")
//            .reviewerNotes("Customer confirmed this was their own transfer")
//            .build();
//
//        mockMvc.perform(post("/api/v1/fraud/review/" + transactionId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(review)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.reviewed").value(true))
//            .andExpect(jsonPath("$.reviewOutcome").value("FALSE_POSITIVE"))
//            .andExpect(jsonPath("$.status").value("APPROVED_AFTER_REVIEW"));
//    }
//
//    @Test
//    @DisplayName("Dashboard stats should return valid counts")
//    void dashboardStatsShouldReturnCounts() throws Exception {
//        mockMvc.perform(get("/api/v1/fraud/dashboard/stats"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.totalTransactions").isNumber())
//            .andExpect(jsonPath("$.flaggedTransactions").isNumber())
//            .andExpect(jsonPath("$.pendingReviews").isNumber())
//            .andExpect(jsonPath("$.blockedTransactions").isNumber());
//    }
//}