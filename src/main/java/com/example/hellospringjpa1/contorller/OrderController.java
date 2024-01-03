package com.example.hellospringjpa1.contorller;

import com.example.hellospringjpa1.domain.Member;
import com.example.hellospringjpa1.domain.item.Item;
import com.example.hellospringjpa1.service.ITemService;
import com.example.hellospringjpa1.service.MemberService;
import com.example.hellospringjpa1.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ITemService iTemService;

    @GetMapping("/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = iTemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {
        Long orderId = orderService.order(memberId, itemId, count);
        return "redirect:/order";
    }

}
