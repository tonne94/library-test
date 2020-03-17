package com.example.test.interfaces.web.rest.mapper;

import com.example.test.domain.model.Contact;
import com.example.test.dto.ContactDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface ContactMapper {

    Contact contactDTOToContact(ContactDTO contactDTO);

    @Mapping(target = "account", ignore = true)
    ContactDTO contactToContactDTO(Contact contact);

    List<ContactDTO> contactsToContactDTOs(List<Contact> contacts);

    List<Contact> contactDTOsToContacts(List<ContactDTO> contactDTOs);
}
