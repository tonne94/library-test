package com.example.test.interfaces.web.rest.mapper;

import com.example.test.domain.model.Account;
import com.example.test.dto.AccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RentRecordMapper.class, ContactMapper.class})
public interface AccountMapper {

    Account accountDTOToAccount(AccountDTO accountDTO);

    AccountDTO accountToAccountDTO(Account account);

    List<AccountDTO> accountsToAccountDTOs(List<Account> accounts);

    List<Account> accountDTOsToAccounts(List<AccountDTO> accountDTOs);

    @FromAccount
    @Mapping(target = "rentRecords", qualifiedBy = FromAccount.class)
    AccountDTO accountToAccountDTOFromAccount(Account account);

    @FromAccount
    default List<AccountDTO> accountsToAccountDTOsFromAccount(List<Account> accounts) {
        if (accounts == null) {
            return null;
        }
        List<AccountDTO> resultList = new ArrayList<>();
        for (Account account : accounts) {
            resultList.add(accountToAccountDTOFromAccount(account));
        }
        return resultList;
    }

    @FromRented
    @Mapping(target = "rentRecords", ignore = true)
    AccountDTO accountToAccountDTOFromRented(Account account);
}
