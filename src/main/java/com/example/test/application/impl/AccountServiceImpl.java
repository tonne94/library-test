package com.example.test.application.impl;

import com.example.test.application.AccountService;
import com.example.test.domain.model.Account;
import com.example.test.domain.model.BookRecord;
import com.example.test.domain.model.Contact;
import com.example.test.domain.model.RentRecord;
import com.example.test.domain.repository.AccountRepository;
import com.example.test.domain.repository.BookRecordRepository;
import com.example.test.domain.repository.RentRecordRepository;
import com.example.test.dto.AccountDTO;
import com.example.test.dto.DocumentDTO;
import com.example.test.interfaces.web.rest.mapper.AccountMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountRepository accountRepository;
    private BookRecordRepository bookRecordRepository;
    private RentRecordRepository rentRecordRepository;
    private AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountMapper accountMapper,
                              BookRecordRepository bookRecordRepository,
                              RentRecordRepository rentRecordRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.bookRecordRepository = bookRecordRepository;
        this.rentRecordRepository = rentRecordRepository;
    }

    @Override
    public Optional<Account> rentBook(Account account, BookRecord bookRecord) {
        RentRecord rentRecord = new RentRecord();
        rentRecord.setAccount(account);
        rentRecord.setBookRecord(bookRecord);
        rentRecord.setRentTime(LocalDateTime.now());
        rentRecord.setReturnTime(LocalDateTime.now().plusDays(30));
        rentRecord = rentRecordRepository.save(rentRecord);
        account.getRentRecords().add(rentRecord);
        Account result = accountRepository.save(account);
        return Optional.of(result);
    }

    @Override
    public Account returnBook(RentRecord rentRecord) {
        rentRecord.setActualReturnTime(LocalDateTime.now());
        rentRecord = rentRecordRepository.save(rentRecord);

        Optional<Account> accountOptional = accountRepository.findById(rentRecord.getAccount().getId());

        return accountOptional.get();
    }

    @Override
    public List<AccountDTO> findAll(boolean onlyOverdue) {
        List<Account> accounts = accountRepository.findAll();
        List<AccountDTO> accountDTOs = accountMapper.accountsToAccountDTOsFromAccount(accounts);
        if(onlyOverdue){
            accountDTOs.removeIf(
                    account -> {
                        account.getRentRecords().removeIf(
                                rentRecord -> rentRecord.getOverdueDays() == 0
                        );
                        return account.getRentRecords().isEmpty();
                    }
            );
        }
        Collections.sort(accountDTOs, Collections.reverseOrder());
        return accountDTOs;
    }

    @Override
    public List<AccountDTO> findAllOnlyRentedBooks(boolean onlyOverdue) {
        List<Account> accounts = accountRepository.findAll();
        List<AccountDTO> accountDTOs = accountMapper.accountsToAccountDTOsFromAccount(accounts);
        accountDTOs.removeIf(
                account -> {
                    account.getRentRecords().removeIf(
                            rentRecord -> rentRecord.getActualReturnTime() != null || (onlyOverdue && rentRecord.getOverdueDays() == 0)
                    );
                    return account.getRentRecords().isEmpty();
                }
        );
        Collections.sort(accountDTOs, Collections.reverseOrder());
        return accountDTOs;
    }

    @Override
    public AccountDTO findByIdOnlyRentedBooks(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            log.error("Account not found, id: " + id);
            return null;
        }
        AccountDTO accountDTO = accountMapper.accountToAccountDTOFromAccount(accountOptional.get());
        accountDTO.getRentRecords().removeIf(
                rentRecord -> rentRecord.getActualReturnTime() != null
        );
        return accountDTO;
    }

    @Transactional
    @Override
    public Optional<Account> createAccount(Account account) {
        List<Contact> contacts = account.getContacts();
        contacts.forEach(contact -> contact.setAccount(account));
        return Optional.of(accountRepository.save(account));
    }

    @Transactional
    @Override
    public Optional<Account> createAccountWithDocumentDTO(DocumentDTO documentDTO) {
        log.info("Preparing headers for Microblink API");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + "ODNkMTBhNjBjOWQ2NGYyMGJiN2ZjZTdjMjcxMTc4MzU6ZTEwZDE5YTYtN2VhOC00ZGM1LTg4ZjktZTZhYjJkYWIxZDBl");

        RestTemplate restTemplate = new RestTemplate();

        log.info("Preparing parameters for Microblink API");
        Map<String, String> params = new HashMap<>();
        params.put("recognizerType", documentDTO.getRecognizerType());
        params.put("imageBase64", documentDTO.getImageBase64());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

        log.info("Executing API call to Microblink API");
        ResponseEntity<String> result = restTemplate.postForEntity("https://api.microblink.com/recognize/execute", entity, String.class);

        if (!result.getBody().contains("rawMRZString")) {
            log.error("rawMRZString not found in result");
            return Optional.empty();
        }

        String rawMRZString = extractrawMRZString(result.getBody());
        List<String> allMatches = getAllMatches(rawMRZString);
        String checkDigitString = extractCheckDigitString(allMatches);

        log.info("Creating account based on extracted info");
        Account account = new Account();
        account.setName(allMatches.get(5) + " " + allMatches.get(6));
        account.setSurname(allMatches.get(4));
        account.setValid(checkDigit(checkDigitString, intToNumber(allMatches.get(3).charAt(0))));

        return Optional.of(accountRepository.save(account));
    }

    @Transactional
    @Override
    public Optional<Account> createAccountWithImage(MultipartFile multipartFile) {
        log.info("Preparing headers for Microblink API");
        File file = null;
        String imageBase64 = "";
        byte[] fileContent = new byte[0];

        log.info("Converting file to base64 string");
        try {
            file = multipartToFile(multipartFile, multipartFile.getOriginalFilename());
            fileContent = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageBase64 = Base64.getEncoder().encodeToString(fileContent);

        log.info("Preparing headers for Microblink API");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + "ODNkMTBhNjBjOWQ2NGYyMGJiN2ZjZTdjMjcxMTc4MzU6ZTEwZDE5YTYtN2VhOC00ZGM1LTg4ZjktZTZhYjJkYWIxZDBl");

        log.info("Getting file extension");
        String fileExtension = getExtensionByStringHandling(file.getName()).orElse(null);
        String imageFormat = "";
        if (fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {
            imageFormat = "data:image/jpeg;base64,";
        } else if (fileExtension.equals("png")) {
            imageFormat = "data:image/png;base64,";
        } else {
            return null;
        }

        log.info("Preparing parameters for Microblink API");
        Map<String, String> params = new HashMap<>();
        params.put("recognizerType", "MRTD");
        params.put("imageBase64", imageFormat + imageBase64);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

        log.info("Executing API call to Microblink API");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.postForEntity("https://api.microblink.com/recognize/execute", entity, String.class);

        if (!result.getBody().contains("rawMRZString")) {
            log.error("rawMRZString not found in result");
            return Optional.empty();
        }

        String rawMRZString = extractrawMRZString(result.getBody());
        List<String> allMatches = getAllMatches(rawMRZString);
        String checkDigitString = extractCheckDigitString(allMatches);

        log.info("Creating account based on extracted info");
        Account account = new Account();
        account.setName(allMatches.get(5) + " " + allMatches.get(6));
        account.setSurname(allMatches.get(4));
        account.setValid(checkDigit(checkDigitString, intToNumber(allMatches.get(3).charAt(0))));

        return Optional.of(accountRepository.save(account));
    }

    @Transactional
    @Override
    public Account updateAccount(Account account) {
        log.info("Update account with id: " + account.getId());
        List<Contact> contacts = account.getContacts();
        contacts.forEach(contact -> contact.setAccount(account));
        return accountRepository.save(account);
    }

    private static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        multipart.transferTo(convFile);
        return convFile;
    }

    private Optional<String> getExtensionByStringHandling(String filename) {
        log.info("Getting extension of a file");
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    private String extractrawMRZString(String result) {
        log.info("Extracting rawMRZString");
        Gson gson = new Gson();
        JsonObject body = gson.fromJson(result, JsonObject.class);
        JsonObject dataObj = body.get("data").getAsJsonObject();
        JsonObject resultObj = dataObj.get("result").getAsJsonObject();
        return resultObj.get("rawMRZString").getAsString();
    }

    private List<String> getAllMatches(String rawMRZString) {
        log.info("Get all matches from result string");
        Pattern pattern = Pattern.compile("([A-Z0-9])+");
        Matcher matcher = pattern.matcher(rawMRZString);
        List<String> allMatches = new ArrayList<String>();
        while (matcher.find()) {
            allMatches.add(matcher.group());
        }
        return allMatches;
    }

    private String extractCheckDigitString(List<String> strings) {
        log.info("Extract check digit string");
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(strings.get(1).substring(3));
        String firstString = strings.get(2).substring(0, strings.get(2).length() - 3);
        String[] firstStringArray = firstString.split("([M|F])");
        stringBuilder.append(firstStringArray[0]);
        stringBuilder.append(firstStringArray[1]);
        return stringBuilder.toString();
    }

    private boolean checkDigit(String checkDigitString, int checkDigit) {
        log.info("Calculating and comparing to check digit");
        int sum = 0;

        int[] weightNumbers = {7, 3, 1};
        int temp = 0;

        for (char ch : checkDigitString.toCharArray()) {
            sum += Character.isDigit(ch) ? intToNumber(ch) * weightNumbers[temp % 3] : charToNumber(ch) * weightNumbers[temp % 3];
            temp++;
        }

        int calculatedCheckDigit = sum % 10;

        return calculatedCheckDigit == checkDigit;
    }

    private static int intToNumber(char chr) {
        return (chr - 48);
    }

    private static int charToNumber(char chr) {
        return (chr - 55);
    }


}
