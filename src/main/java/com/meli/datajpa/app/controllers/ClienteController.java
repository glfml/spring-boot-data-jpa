package com.meli.datajpa.app.controllers;

import com.meli.datajpa.app.models.entity.Cliente;
import com.meli.datajpa.app.service.IClienteService;
import com.meli.datajpa.app.util.paginator.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping(value="/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename)
    {
        Path path = Paths.get("uploads").resolve(filename).toAbsolutePath();
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("No se puede cargar la foto");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping(value = "/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

        Cliente cliente = clienteService.findOne(id);
        if (null == cliente) {
            flash.addFlashAttribute("error", "El cliente no existe");
            return "redirect:/listar";
        }

        model.put("cliente", cliente);
        model.put("titulo", "Detalle de cliente: " + cliente.getNombre());

        return "ver";
    }

    @RequestMapping(value = "listar", method = RequestMethod.GET)
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Pageable pageRequest = PageRequest.of(page, 2);
        model.addAttribute("titulo", "Listado de clientes");
        Page<Cliente> clientes = clienteService.findAll(pageRequest);

        PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

        model.addAttribute("clientes", clientes);
        model.addAttribute("pager", pageRender);

        return "listar";
    }

    @RequestMapping(value = "/form")
    public String crear(Map<String, Object> model) {
        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        model.put("titulo", "Formulario de cliente");
        return "form";
    }

    @RequestMapping(value = "/form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model) {
        Cliente cliente;
        if (id > 0) {
            cliente = clienteService.findOne(id);
        } else {
            return "redirect:/listar";
        }

        model.put("cliente", cliente);
        model.put("titulo", "Editar cliente");

        return "form";
    }

    @RequestMapping(value="/form", method = RequestMethod.POST)
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile photo, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Completar formulario, tiene errores");
            model.addAttribute("errors", "Cliente no creado/editado");

            return "form";
        }

        if (!photo.isEmpty()) {
            String rootPath = "/opt/uploads";
            try {
                byte[] bytes = photo.getBytes();
                Path finalPath = Paths.get(rootPath + "//" + photo.getOriginalFilename());
                Files.write(finalPath, bytes);
                flash.addFlashAttribute("info", "Archivo procesado correctamente: " + photo.getOriginalFilename());
                cliente.setPhoto(photo.getOriginalFilename());
            } catch (IOException e) {

            }
        }

        clienteService.save(cliente);
        flash.addFlashAttribute("success", "Cliente creado/editado");

        return "redirect:listar";
    }

    @RequestMapping(value = "/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id) {
        if (id > 0) {
            clienteService.delete(id);
        }

        return "redirect:/listar";
    }
}
