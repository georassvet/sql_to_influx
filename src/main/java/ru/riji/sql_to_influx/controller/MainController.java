package ru.riji.sql_to_influx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.riji.sql_to_influx.dao.DbDao;
import ru.riji.sql_to_influx.dao.InfluxDao;
import ru.riji.sql_to_influx.dao.IntervalDao;
import ru.riji.sql_to_influx.dao.SqlTaskDao;
import ru.riji.sql_to_influx.form.ConnectForm;
import ru.riji.sql_to_influx.form.DeleteForm;
import ru.riji.sql_to_influx.form.IntervalForm;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.service.SqlTaskService;

@Controller
public class MainController {
    @Autowired
    private SqlTaskService sqlTaskService;

    @Autowired
    private SqlTaskDao dao;
    @Autowired
    private IntervalDao intervalDao;
    @Autowired
    private InfluxDao influxDao;
    @Autowired
    private DbDao dbDao;

    @GetMapping(value ={"/","/index"})
    public String index(Model model){
        return "sql";
    }

    @GetMapping(value ={"/settings"})
    public String settings(Model model){
        return "settings";
    }

    @GetMapping(value = "/addSqlTask")
    public String addSqlTask(Model model){
        model.addAttribute("influxes", influxDao.getAll());
        model.addAttribute("dbs", dbDao.getAll());
        model.addAttribute("intervals", intervalDao.getAll());
        model.addAttribute("form", new SqlTaskForm());
        return "addSqlTask";
    }

    @GetMapping(value = "/editSqlTask/{id}")
    public String editSqlTask(Model model, @PathVariable("id") int id){
        model.addAttribute("influxes", influxDao.getAll());
        model.addAttribute("dbs", dbDao.getAll());
        model.addAttribute("intervals", intervalDao.getAll());
        model.addAttribute("form", new SqlTaskForm(dao.getById(id)));
        return "addSqlTask";
    }
    @PostMapping(value = "/addSqlTask")
    public String addSqlTask(Model model, SqlTaskForm form){
        if(form.getId() == 0){
            sqlTaskService.add(form);
        }else{
            sqlTaskService.update(form);
        }
        return "redirect:/index";
    }
    @GetMapping(value = "/deleteSqlTask")
    public String deleteSqlTask(Model model, int id){
        sqlTaskService.delete(id);
        return "redirect:/addInterval";
    }
    @GetMapping(value = "/addInterval")
    public String addInterval(Model model){
        model.addAttribute("form", new IntervalForm());
        model.addAttribute("items", intervalDao.getAll());
        return "addInterval";
    }
    @GetMapping(value = "/editInterval/{id}")
    public String editInterval(Model model, @PathVariable("id") int id){
        model.addAttribute("form", new IntervalForm(intervalDao.getById(id)));
        model.addAttribute("items", intervalDao.getAll());
        return "addInterval";
    }
    @PostMapping(value = "/addInterval")
    public String addInterval(Model model, IntervalForm form){
        if(form.getId() == 0){
            intervalDao.add(form);
        }else{
            intervalDao.update(form);
        }
        return "redirect:/addInterval";
    }
    @GetMapping(value = "/deleteInterval")
    public String deleteInterval(Model model, int id){
        intervalDao.delete(id);
        return "redirect:/addInterval";
    }
    @GetMapping(value = "/addInflux")
    public String addInflux(Model model){
        model.addAttribute("form", new ConnectForm());
        model.addAttribute("items", influxDao.getAll());
        return "addInflux";
    }
    @GetMapping(value = "/editInflux/{id}")
    public String editInflux(Model model, @PathVariable("id") int id){
        model.addAttribute("form", new ConnectForm(influxDao.getById(id)));
        model.addAttribute("items", influxDao.getAll());
        return "addInflux";
    }
    @PostMapping(value = "/addInflux")
    public String addInflux(Model model, ConnectForm form){
        if(form.getId() == 0){
            influxDao.add(form);
        }else{
            influxDao.update(form);
        }
        return "redirect:/addInflux";
    }
    @GetMapping(value = "/deleteInflux")
    public String addInflux(Model model, int id){
         influxDao.delete(id);
        return "redirect:/addInflux";
    }
    @GetMapping(value = "/addDbConnect")
    public String addDbConnect(Model model){
        model.addAttribute("form", new ConnectForm());
        model.addAttribute("items", dbDao.getAll());
        return "addDbConnect";
    }
    @GetMapping(value = "/editDbConnect/{id}")
    public String editDbConnect(Model model, @PathVariable("id") int id){
        model.addAttribute("form", new ConnectForm(dbDao.getById(id)));
        model.addAttribute("items", dbDao.getAll());
        return "addDbConnect";
    }
    @PostMapping(value = "/addDbConnect")
    public String addDbConnect(Model model, ConnectForm form){
        if(form.getId() == 0){
            dbDao.add(form);
        }else{
            dbDao.update(form);
        }
        return "redirect:/addDbConnect";
    }
    @GetMapping(value = "/deleteDbConnect")
    public String deleteDbConnect(Model model, int id){
        dbDao.delete(id);
        return "redirect:/addDbConnect";
    }
}
