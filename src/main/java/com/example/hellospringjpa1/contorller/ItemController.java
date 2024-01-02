package com.example.hellospringjpa1.contorller;

import com.example.hellospringjpa1.domain.item.Book;
import com.example.hellospringjpa1.domain.item.Item;
import com.example.hellospringjpa1.service.ITemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ITemService iTemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    private String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setPrice(form.getPrice());
        book.setAuthor(form.getAuthor());
        book.setStockQuantity(form.getStockQuantity());
        book.setIsbn(form.getIsbn());

        iTemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = iTemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) iTemService.findById(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setAuthor(item.getAuthor());
        form.setStockQuantity(item.getStockQuantity());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable Long itemId) {

        // 트랜잭션이 있다고 쳐도 해당 객체는 준영속 엔티티이기 때문에 영속성 컨텍스트가 관리하지 않아
        // 더티 채킹이 일어나지 않음 (이미 식별자를 가지고 있음. 디비에 한번 넣었다 빠진 엔티티)
        // 준영속 엔티티를 수정하는 방법 / 변경감지 사용, 머지 사용
        // 1. 영속 상태의 엔티티를 디비에서 조회 후 더티 채팅의 대상이 되게 한다.
        // 2. merge, 는 준영속 상태의 엔티티를 영속 상태로 변경해준다.
        // merge 쓰면 모든 데이터를 다 밀어넣어줘서 가급적 쓰지말자
        // 결론 -> 엔티티를 변경할 때는 항상 변경 감지를 사용하자
        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        iTemService.saveItem(book);

        return "redirect:/items";
    }
}
