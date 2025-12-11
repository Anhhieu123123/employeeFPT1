package com.example.employeefpt.controller;



import com.example.employeefpt.model.Employee;
import com.example.employeefpt.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    

    @GetMapping({"", "/", "/list"})
    public String listEmployees(Model model) {
        List<Employee> employees = employeeService.getEmployees();
        
        model.addAttribute("employees", employees);
        model.addAttribute("totalEmployees", employees.size());
        model.addAttribute("pageTitle", "Danh sách Nhân viên FPT");
        
        return "employee-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Thêm Nhân viên Mới");
        model.addAttribute("formAction", "add");
        
        return "employee-form";
    }

    @PostMapping("/add")
    public String addEmployee(@Valid @ModelAttribute("employee") Employee employee,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "employee-form";
        }
        
        try {
            Employee savedEmployee = employeeService.addEmployee(employee);
            
            redirectAttributes.addFlashAttribute("successMessage",
                "Thanh cong " + savedEmployee.getName());
            
            return "redirect:/employees";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Lỗi khi thêm nhân viên: " + e.getMessage());
            
            return "redirect:/employees/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        
        if (employee.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Không tìm thấy nhân viên với ID: " + id);
            return "redirect:/employees";
        }
        
        model.addAttribute("employee", employee.get());
        model.addAttribute("pageTitle", "Cập nhật Thông tin Nhân viên");
        model.addAttribute("formAction", "edit");
        
        return "employee-form";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("employee") Employee employee,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "employee-form";
        }
        
        try {
            employee.setId(id);

            Employee updatedEmployee = employeeService.updateEmployee(employee);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã cập nhật thông tin nhân viên: " + updatedEmployee.getName());
            
            return "redirect:/employees";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Lỗi khi cập nhật nhân viên: " + e.getMessage());
            
            return "redirect:/employees/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        
        try {
            Optional<Employee> employee = employeeService.getEmployeeById(id);
            
            if (employee.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Không tìm thấy nhân viên: " + id);
                return "redirect:/employees";
            }
            
            employeeService.deleteEmployee(id);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã xóa nhân viên: " + employee.get().getName());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Lỗi khi xóa nhân viên: " + e.getMessage());
        }
        
        return "redirect:/employees";
    }

    @GetMapping("/search")
    public String searchEmployees(@RequestParam(value = "keyword", required = false) String keyword,
                                  Model model) {
        
        List<Employee> employees;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            employees = employeeService.searchEmployeesByName(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            employees = employeeService.getEmployees();
        }



        model.addAttribute("employees", employees);
        model.addAttribute("totalEmployees", employees.size());
        model.addAttribute("pageTitle", "Tìm kiếm Nhân viên");
        
        return "employee-list";
    }

    @GetMapping("/view/{id}")
    public String viewEmployee(@PathVariable("id") Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        
        if (employee.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Không tìm thấy nhân viên : " + id);
            return "redirect:/employees";
        }
        
        model.addAttribute("employee", employee.get());
        model.addAttribute("pageTitle", "Chi tiết Nhân viên");
        
        return "employee-detail";
    }
}
