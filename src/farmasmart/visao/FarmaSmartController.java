package farmasmart.visao;

import farmasmart.Cliente;
import farmasmart.Farmaceutico;
import farmasmart.FormaPagamento;
import farmasmart.Medico;
import farmasmart.Medicamento;
import farmasmart.MedicamentoComum;
import farmasmart.MedicamentoControlado;
import farmasmart.MedicamentoGenerico;
import farmasmart.NivelRisco;
import farmasmart.Receita;
import farmasmart.StatusValidacao;
import farmasmart.TipoReceita;
import farmasmart.Venda;
import farmasmart.servico.MedicamentoServico;
import farmasmart.servico.Servico;
import farmasmart.servico.VendaServico;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicReference;

public class FarmaSmartController {

    @FXML
    private VBox contentArea;

    private final EnumMap<Section, Adapter<?, ?>> adapters = new EnumMap<>(Section.class);
    private Adapter<?, ?> activeAdapter;
    private Section activeSection;
    private TableView<Row> table;
    private TextField searchField;
    private Label resultLabel;
    private Label feedbackLabel;
    private Button expiringButton;
    private Button backFromExpiringButton;
    private List<?> sourceOverride;
    private Venda activeSale;

    @FXML
    private void initialize() {
        showDashboard();
    }

    public void configurar(Servico<Cliente, String> clientes,
                          Servico<Farmaceutico, String> farmaceuticos,
                          Servico<Medico, String> medicos,
                          MedicamentoServico medicamentos,
                          Servico<Receita, Long> receitas,
                          VendaServico vendas) {
        adapters.put(Section.CLIENTES, adapter(Cliente.class, "Clientes",
                List.of("ID", "Nome", "CPF", "Nascimento"), clientes, Cliente::getId,
                c -> List.of(c.getId(), c.getNome(), c.getCpf(), c.getDataNascimento().toString()),
                this::clienteForm, true));
        adapters.put(Section.FARMACEUTICOS, adapter(Farmaceutico.class, "Farmacêuticos",
                List.of("ID", "Nome", "CPF", "CRF"), farmaceuticos, Farmaceutico::getId,
                f -> List.of(f.getId(), f.getNome(), f.getCpf(), value(f.getCrf())),
                this::farmaceuticoForm, true));
        adapters.put(Section.MEDICOS, adapter(Medico.class, "Médicos",
                List.of("ID", "Nome", "CPF", "CRM"), medicos, Medico::getId,
                m -> List.of(m.getId(), m.getNome(), m.getCpf(), value(m.getCrm())),
                this::medicoForm, true));
        adapters.put(Section.MEDICAMENTOS, adapter(Medicamento.class, "Medicamentos",
                List.of("ID", "Nome comercial", "Princípio ativo", "Tipo", "Preço", "Estoque", "Validade", "Validação"),
                medicamentos, Medicamento::getId,
                m -> List.of(m.getId(), m.getNome(), m.getPrincipioAtivo(), medicineType(m),
                        money(m.getPreco()), String.valueOf(m.getQuantidadeEstoque()),
                        m.getDataValidade().toString(), m.getValidacao().getStatus().toString()),
                current -> medicamentoForm(current, medicamentos), true));
        adapters.put(Section.RECEITAS, adapter(Receita.class, "Receitas",
                List.of("ID", "Paciente", "Médico", "Emissão", "Validade", "Tipo", "Itens", "Situação"),
                receitas, Receita::getId,
                r -> List.of(String.valueOf(r.getId()), r.getCliente().getNome(), r.getMedico().getNome(),
                        r.getDataEmissao().toString(), r.getDataLimite().toString(), r.getTipo().toString(),
                        String.join(", ", r.getItens().stream()
                                .map(i -> i.getPrincipioAtivo() + " (" + i.getQuantidadeRestante() + "/" +
                                        i.getQuantidadePrescrita() + ")").toList()),
                        r.isValida(LocalDate.now()) ? "Válida" : "Indisponível"),
                current -> receitaForm(current, clientes, medicos), true));
        adapters.put(Section.VENDAS, adapter(Venda.class, "Vendas",
                List.of("ID", "Data", "Situação", "Cliente", "Farmacêutico", "Itens", "Receita", "Total", "Pagamento"),
                vendas, Venda::getId,
                v -> List.of(v.getId(), v.getData().toString(), v.getStatus().toString(),
                        v.getCliente() == null ? "Balcão" : v.getCliente().getNome(),
                        v.getFarmaceutico().getNome(),                         String.join(", ", v.getResumoItens()),
                        v.getReceita() == null ? "—" : String.valueOf(v.getReceita().getId()),
                        "R$ " + money(v.calcularTotal()),
                        v.getFormaPagamento() == null ? "—" : v.getFormaPagamento().toString()),
                current -> Optional.empty(), false));
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        activeSection = null;
        activeAdapter = null;
        sourceOverride = null;
        contentArea.setAlignment(javafx.geometry.Pos.CENTER);
        if (!contentArea.getStyleClass().contains("entry-screen")) {
            contentArea.getStyleClass().add("entry-screen");
        }
        contentArea.getChildren().clear();

        Label eyebrow = new Label("PAINEL DE CONTROLE");
        eyebrow.getStyleClass().add("dashboard-eyebrow");
        Label title = new Label("FARMASMART");
        title.getStyleClass().add("dashboard-brand");
        title.setMaxWidth(Region.USE_PREF_SIZE);
        Region brandAccent = new Region();
        brandAccent.getStyleClass().add("brand-accent");
        Label copy = new Label("Gestão farmacêutica inteligente: acompanhe seus cadastros e acesse cada área em um só lugar.");
        copy.getStyleClass().add("hero-copy");
        copy.setWrapText(true);
        copy.setMaxWidth(680);
        VBox hero = new VBox(12, eyebrow, title, brandAccent, copy);
        hero.setAlignment(javafx.geometry.Pos.CENTER);
        hero.setMaxWidth(Double.MAX_VALUE);
        hero.getStyleClass().add("hero-card");

        GridPane quickActions = new GridPane();
        quickActions.setAlignment(javafx.geometry.Pos.CENTER);
        quickActions.setMaxWidth(Double.MAX_VALUE);
        quickActions.setHgap(18);
        quickActions.setVgap(18);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / 3);
            column.setHgrow(Priority.ALWAYS);
            quickActions.getColumnConstraints().add(column);
        }
        Button[] destinations = {
                dashboardButton("▦  Medicamentos", this::showMedicamentos),
                dashboardButton("▤  Receitas", this::showReceitas),
                dashboardButton("⚕  Médicos", this::showMedicos),
                dashboardButton("✚  Farmacêuticos", this::showFarmaceuticos),
                dashboardButton("▣  Vendas", this::showVendas),
                dashboardButton("♙  Clientes", this::showClientes)
        };
        for (int i = 0; i < destinations.length; i++) {
            GridPane.setHgrow(destinations[i], Priority.ALWAYS);
            quickActions.add(destinations[i], i % 3, i / 3);
        }
        VBox quickPanel = new VBox(12, quickActions);
        quickPanel.setAlignment(javafx.geometry.Pos.CENTER);
        quickPanel.getStyleClass().add("panel");
        quickPanel.getStyleClass().add("entry-actions-panel");
        quickPanel.setPadding(new javafx.geometry.Insets(30));
        VBox entryContent = new VBox(18, hero, quickPanel);
        entryContent.setAlignment(javafx.geometry.Pos.CENTER);
        entryContent.setPrefWidth(1200);
        entryContent.setMaxWidth(1200);
        contentArea.getChildren().add(entryContent);
    }

    private Button dashboardButton(String text, Runnable action) {
        Button button = button(text, action, "dashboard-link");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(86);
        return button;
    }

    @FXML
    private void showClientes() { showSection(Section.CLIENTES); }

    @FXML
    private void showFarmaceuticos() { showSection(Section.FARMACEUTICOS); }

    @FXML
    private void showMedicos() { showSection(Section.MEDICOS); }

    @FXML
    private void showMedicamentos() { showSection(Section.MEDICAMENTOS); }

    @FXML
    private void showReceitas() { showSection(Section.RECEITAS); }

    @FXML
    private void showVendas() { showSection(Section.VENDAS); }

    private void showSection(Section section) {
        activeSection = section;
        activeAdapter = adapters.get(section);
        sourceOverride = null;
        activeSale = null;
        contentArea.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        contentArea.getStyleClass().remove("entry-screen");
        expiringButton = null;
        backFromExpiringButton = null;
        if (activeAdapter == null) {
            showDashboard();
            return;
        }

        contentArea.getChildren().clear();
        Label title = new Label(activeAdapter.title);
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(section.description);
        subtitle.getStyleClass().add("page-subtitle");
        VBox heading = new VBox(5, title, subtitle);

        HBox actions = new HBox(9);
        actions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        actions.getChildren().add(button("← Tela inicial", this::showDashboard, "secondary-button"));
        Button add = button("+ Novo cadastro", this::createRecord, "primary-button");
        Button edit = button("Editar selecionado", this::updateRecord, "secondary-button");
        Button delete = button("Excluir", this::deleteRecord, "danger-button");
        actions.getChildren().addAll(add, edit, delete);
        edit.setDisable(!activeAdapter.updatable);

        if (section == Section.MEDICAMENTOS) {
            expiringButton = button("Próximos do vencimento", this::showExpiring, "green-button");
            backFromExpiringButton = button("← Voltar aos medicamentos",
                    () -> showSection(Section.MEDICAMENTOS), "secondary-button");
            backFromExpiringButton.setVisible(false);
            backFromExpiringButton.setManaged(false);
            actions.getChildren().addAll(expiringButton, backFromExpiringButton);
            actions.getChildren().add(button("Registrar validação", this::registerValidation, "secondary-button"));
        } else if (section == Section.VENDAS) {
            add.setText("+ Abrir venda");
            actions.getChildren().add(button("Abrir venda selecionada", this::openSelectedSale, "green-button"));
            edit.setVisible(false);
            edit.setManaged(false);
        }

        searchField = new TextField();
        searchField.setPromptText("Pesquisar nesta tabela...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refreshRows());
        resultLabel = new Label();
        resultLabel.getStyleClass().add("page-subtitle");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox filterRow = new HBox(10, searchField, spacer, resultLabel);
        filterRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("Nenhum registro encontrado."));
        for (int i = 0; i < section.columns.size(); i++) {
            final int columnIndex = i;
            TableColumn<Row, String> column = new TableColumn<>(section.columns.get(i));
            column.setCellValueFactory(cell ->
                    new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().values.get(columnIndex)));
            column.setMinWidth(section == Section.MEDICAMENTOS && i == 2 ? 150 : 78);
            table.getColumns().add(column);
        }
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        feedbackLabel = new Label("Selecione uma linha para editar, excluir ou realizar ações.");
        feedbackLabel.getStyleClass().add("page-subtitle");
        contentArea.getChildren().addAll(heading, actions, filterRow, table, feedbackLabel);
        refreshRows();
    }

    private void refreshRows() {
        if (table == null || activeAdapter == null) {
            return;
        }
        List<?> source = sourceOverride == null ? activeAdapter.list() : sourceOverride;
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        ObservableList<Row> rows = FXCollections.observableArrayList();
        for (Object entity : source) {
            Row row = activeAdapter.toRow(entity);
            if (query.isEmpty() || row.values.stream().anyMatch(value ->
                    value.toLowerCase(Locale.ROOT).contains(query))) {
                rows.add(row);
            }
        }
        table.setItems(rows);
        if (resultLabel != null) {
            resultLabel.setText(rows.size() + (rows.size() == 1 ? " registro" : " registros"));
        }
    }

    private void createRecord() {
        if (activeSection == Section.VENDAS) {
            createSale();
            return;
        }
        withErrors(() -> {
            create(activeAdapter);
            refreshRows();
            feedback("Cadastro realizado com sucesso.");
        });
    }

    private void updateRecord() {
        if (activeAdapter == null || !activeAdapter.updatable) {
            return;
        }
        Row selected = selectedRow();
        if (selected == null) {
            return;
        }
        withErrors(() -> {
            update(activeAdapter, selected.entity);
            refreshRows();
            feedback("Registro atualizado com sucesso.");
        });
    }

    private void deleteRecord() {
        Row selected = selectedRow();
        if (selected == null || activeAdapter == null) {
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja realmente excluir o registro selecionado?",
                ButtonType.CANCEL, ButtonType.OK);
        confirmation.setTitle("Confirmar exclusão");
        confirmation.setHeaderText("Esta ação não pode ser desfeita.");
        own(confirmation);
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        withErrors(() -> {
            delete(activeAdapter, selected.entity);
            refreshRows();
            feedback("Registro excluído com sucesso.");
        });
    }

    private void showExpiring() {
        withErrors(() -> {
            sourceOverride = ((MedicamentoServico) activeAdapter.service).proximosDoVencimento(60);
            refreshRows();
            expiringButton.setVisible(false);
            expiringButton.setManaged(false);
            backFromExpiringButton.setVisible(true);
            backFromExpiringButton.setManaged(true);
            feedback("Exibindo medicamentos que vencem nos próximos 60 dias.");
        });
    }

    private void registerValidation() {
        Row row = selectedRow();
        if (row == null) return;
        Medicamento medicamento = (Medicamento) row.entity;
        ComboBox<StatusValidacao> status = combo(StatusValidacao.values());
        status.setValue(medicamento.getValidacao().getStatus());
        TextField organization = new TextField("openFDA");
        GridPane form = formGrid();
        addField(form, 0, "Resultado", status);
        addField(form, 1, "Órgão validador", organization);
        choose("Registrar validação sanitária", form, () -> {
            ((MedicamentoServico) activeAdapter.service).registrarValidacao(
                    medicamento.getId(), status.getValue(), organization.getText().trim());
            return true;
        }).ifPresent(done -> {
            refreshRows();
            feedback("Validação sanitária atualizada.");
        });
    }

    private void createSale() {
        Servico<Farmaceutico, String> pharmacists = service(Section.FARMACEUTICOS);
        Servico<Cliente, String> clients = service(Section.CLIENTES);
        ComboBox<Farmaceutico> pharmacist = combo(pharmacists.listar());
        ComboBox<Cliente> client = combo(clients.listar());
        pharmacist.setConverter(converter(Farmaceutico::getNome, Farmaceutico::getId));
        client.setConverter(converter(Cliente::getNome, Cliente::getId));
        if (!pharmacist.getItems().isEmpty()) pharmacist.getSelectionModel().selectFirst();
        TextField id = new TextField();
        id.setPromptText("Ex.: V-1001");
        GridPane form = formGrid();
        addField(form, 0, "Identificador", id);
        addField(form, 1, "Farmacêutico", pharmacist);
        addField(form, 2, "Cliente (opcional)", client);
        choose("Abrir nova venda", form, () -> {
            if (pharmacist.getValue() == null) {
                throw new IllegalArgumentException("Cadastre um farmacêutico antes de abrir uma venda.");
            }
            return new Venda(id.getText().trim(), pharmacist.getValue(), client.getValue());
        }).ifPresent(venda -> withErrors(() -> {
            service(Section.VENDAS).cadastrar(venda);
            showSaleWorkspace(venda);
        }));
    }

    private void openSelectedSale() {
        Venda sale = selectedSale();
        if (sale != null) {
            showSaleWorkspace(sale);
        }
    }

    private void showSaleWorkspace(Venda sale) {
        activeSection = Section.VENDAS;
        activeAdapter = adapters.get(Section.VENDAS);
        activeSale = sale;
        sourceOverride = null;
        contentArea.getChildren().clear();

        Button back = button("← Voltar às vendas", this::showVendas, "secondary-button");
        Label title = new Label("Venda " + sale.getId());
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Adicione os produtos, confira o total e finalize o pagamento.");
        subtitle.getStyleClass().add("page-subtitle");
        VBox heading = new VBox(5, title, subtitle);

        HBox details = new HBox(24,
                detailCard("STATUS", sale.getStatus().toString()),
                detailCard("CLIENTE", sale.getCliente() == null ? "Balcão" : sale.getCliente().getNome()),
                detailCard("FARMACÊUTICO", sale.getFarmaceutico().getNome()),
                detailCard("RECEITA", sale.getReceita() == null ? "Não anexada" : "#" + sale.getReceita().getId()));
        details.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));

        Label itemsTitle = new Label("Itens da venda");
        itemsTitle.getStyleClass().add("page-title");
        TableView<String> items = new TableView<>();
        items.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        items.setPlaceholder(new Label("Nenhum item adicionado. Use “Adicionar medicamento” para iniciar."));
        TableColumn<String, String> itemColumn = new TableColumn<>("Medicamento e quantidade");
        itemColumn.setCellValueFactory(value -> new javafx.beans.property.ReadOnlyStringWrapper(value.getValue()));
        items.getColumns().add(itemColumn);
        items.setItems(FXCollections.observableArrayList(sale.getResumoItens()));
        items.getStyleClass().add("table-view");
        VBox.setVgrow(items, Priority.ALWAYS);

        Label totalLabel = new Label("Total: R$ " + money(sale.calcularTotal()));
        totalLabel.getStyleClass().add("sale-total");
        HBox actions = new HBox(10);
        Button addItem = button("+ Adicionar medicamento", () -> addSaleItem(sale), "green-button");
        Button prescription = button("Anexar receita", () -> attachPrescription(sale), "secondary-button");
        Button payment = button("Finalizar pagamento", () -> finishSale(sale), "primary-button");
        Button cancel = button("Cancelar venda", () -> cancelSale(sale), "danger-button");
        actions.getChildren().addAll(addItem, prescription, payment, cancel);
        boolean open = sale.getStatus() == farmasmart.StatusVenda.ABERTA;
        addItem.setDisable(!open);
        prescription.setDisable(!open || sale.getCliente() == null);
        payment.setDisable(!open);
        cancel.setDisable(!open);
        feedbackLabel = new Label(open
                ? "Adicione os itens e finalize a venda quando estiver pronta."
                : "Esta venda não está mais aberta. Use o botão voltar para retornar à lista.");
        feedbackLabel.getStyleClass().add("page-subtitle");
        contentArea.getChildren().addAll(back, heading, details, itemsTitle, items, totalLabel, actions, feedbackLabel);
    }

    private VBox detailCard(String label, String value) {
        Label caption = new Label(label);
        caption.getStyleClass().add("stat-label");
        Label detail = new Label(value);
        detail.getStyleClass().add("sale-detail");
        VBox card = new VBox(7, caption, detail);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void addSaleItem(Venda sale) {
        ComboBox<Medicamento> medication = combo(this.<Medicamento, String>service(Section.MEDICAMENTOS).listar());
        medication.setConverter(converter(Medicamento::getNome, Medicamento::getId));
        if (!medication.getItems().isEmpty()) medication.getSelectionModel().selectFirst();
        TextField quantity = new TextField("1");
        GridPane form = formGrid();
        addField(form, 0, "Medicamento", medication);
        addField(form, 1, "Quantidade", quantity);
        choose("Adicionar item à venda " + sale.getId(), form, () -> {
            if (medication.getValue() == null) throw new IllegalArgumentException("Cadastre um medicamento primeiro.");
            return Integer.parseInt(required(quantity, "Quantidade"));
        }).ifPresent(qty -> withErrors(() -> {
            ((VendaServico) activeAdapter.service).adicionarItem(sale.getId(), medication.getValue().getId(), qty);
            showSaleWorkspace(sale);
            feedback("Item adicionado à venda.");
        }));
    }

    private void attachPrescription(Venda sale) {
        ComboBox<Receita> prescription = combo(this.<Receita, Long>service(Section.RECEITAS).listar());
        prescription.setConverter(converter(r -> "Receita " + r.getId() + " — " + r.getCliente().getNome(),
                r -> String.valueOf(r.getId())));
        if (!prescription.getItems().isEmpty()) prescription.getSelectionModel().selectFirst();
        GridPane form = formGrid();
        addField(form, 0, "Receita médica", prescription);
        choose("Anexar receita à venda " + sale.getId(), form, () -> {
            if (prescription.getValue() == null) throw new IllegalArgumentException("Cadastre uma receita primeiro.");
            return prescription.getValue();
        }).ifPresent(receita -> withErrors(() -> {
            ((VendaServico) activeAdapter.service).anexarReceita(sale.getId(), receita.getId());
            showSaleWorkspace(sale);
            feedback("Receita anexada à venda.");
        }));
    }

    private void finishSale(Venda sale) {
        ComboBox<FormaPagamento> payment = combo(FormaPagamento.values());
        payment.getSelectionModel().selectFirst();
        GridPane form = formGrid();
        addField(form, 0, "Forma de pagamento", payment);
        choose("Finalizar venda " + sale.getId(), form, () -> payment.getValue())
                .ifPresent(forma -> withErrors(() -> {
                    BigDecimal total = ((VendaServico) activeAdapter.service).finalizar(sale.getId(), forma);
                    showSaleCompleted(sale, total);
                }));
    }

    private void showSaleCompleted(Venda sale, BigDecimal total) {
        activeSection = null;
        activeAdapter = null;
        activeSale = sale;
        sourceOverride = null;
        contentArea.getChildren().clear();
        contentArea.setAlignment(javafx.geometry.Pos.CENTER);

        Label check = new Label("✓");
        check.getStyleClass().add("sale-success-icon");
        Label title = new Label("venda finalizada");
        title.getStyleClass().add("sale-success-title");
        Label description = new Label("O pagamento foi confirmado e o estoque atualizado.");
        description.getStyleClass().add("hero-copy");
        Label saleNumber = new Label("Venda " + sale.getId());
        saleNumber.getStyleClass().add("sale-detail");
        Label totalLabel = new Label("Total pago: R$ " + money(total));
        totalLabel.getStyleClass().add("sale-total");
        Button home = button("Voltar à tela inicial", this::showDashboard, "primary-button");
        VBox confirmation = new VBox(16, check, title, description, saleNumber, totalLabel, home);
        confirmation.setAlignment(javafx.geometry.Pos.CENTER);
        confirmation.getStyleClass().add("sale-success-card");
        contentArea.getChildren().add(confirmation);
    }

    private void cancelSale(Venda sale) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancelar a venda " + sale.getId() + "?", ButtonType.CANCEL, ButtonType.OK);
        confirmation.setTitle("Cancelar venda");
        confirmation.setHeaderText("A venda aberta será marcada como cancelada.");
        own(confirmation);
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            withErrors(() -> {
                ((VendaServico) activeAdapter.service).cancelar(sale.getId());
                showSaleWorkspace(sale);
                feedback("Venda cancelada.");
            });
        }
    }

    private Optional<Cliente> clienteForm(Optional<Cliente> current) {
        Cliente existing = current.orElse(null);
        TextField id = text(existing == null ? "" : existing.getId(), "Identificador");
        TextField name = text(existing == null ? "" : existing.getNome(), "Nome completo");
        TextField cpf = text(existing == null ? "" : existing.getCpf(), "Somente os 11 dígitos");
        DatePicker birth = date(existing == null ? null : existing.getDataNascimento());
        if (existing != null) id.setDisable(true);
        GridPane form = formGrid();
        addField(form, 0, "ID", id);
        addField(form, 1, "Nome", name);
        addField(form, 2, "CPF", cpf);
        addField(form, 3, "Data de nascimento", birth);
        return choose(existing == null ? "Novo cliente" : "Editar cliente", form,
                () -> new Cliente(required(id, "ID"), required(name, "Nome"),
                        required(cpf, "CPF"), required(birth, "Data de nascimento")));
    }

    private Optional<Farmaceutico> farmaceuticoForm(Optional<Farmaceutico> current) {
        Farmaceutico existing = current.orElse(null);
        TextField id = text(existing == null ? "" : existing.getId(), "Identificador");
        TextField name = text(existing == null ? "" : existing.getNome(), "Nome completo");
        TextField cpf = text(existing == null ? "" : existing.getCpf(), "Somente os 11 dígitos");
        TextField crf = text(existing == null ? "" : value(existing.getCrf()), "Registro CRF");
        if (existing != null) id.setDisable(true);
        GridPane form = formGrid();
        addField(form, 0, "ID", id);
        addField(form, 1, "Nome", name);
        addField(form, 2, "CPF", cpf);
        addField(form, 3, "CRF", crf);
        return choose(existing == null ? "Novo farmacêutico" : "Editar farmacêutico", form, () -> {
            Farmaceutico result = new Farmaceutico(required(id, "ID"), required(name, "Nome"),
                    required(cpf, "CPF"));
            result.setCrf(required(crf, "CRF"));
            return result;
        });
    }

    private Optional<Medico> medicoForm(Optional<Medico> current) {
        Medico existing = current.orElse(null);
        TextField id = text(existing == null ? "" : existing.getId(), "Identificador");
        TextField name = text(existing == null ? "" : existing.getNome(), "Nome completo");
        TextField cpf = text(existing == null ? "" : existing.getCpf(), "Somente os 11 dígitos");
        TextField crm = text(existing == null ? "" : value(existing.getCrm()), "Registro CRM");
        if (existing != null) id.setDisable(true);
        GridPane form = formGrid();
        addField(form, 0, "ID", id);
        addField(form, 1, "Nome", name);
        addField(form, 2, "CPF", cpf);
        addField(form, 3, "CRM", crm);
        return choose(existing == null ? "Novo médico" : "Editar médico", form, () -> {
            Medico result = new Medico(required(id, "ID"), required(name, "Nome"),
                    required(cpf, "CPF"));
            result.setCrm(required(crm, "CRM"));
            return result;
        });
    }

    private Optional<Medicamento> medicamentoForm(Optional<Medicamento> current,
                                                   MedicamentoServico service) {
        Medicamento existing = current.orElse(null);
        TextField id = text(existing == null ? "" : existing.getId(), "Identificador");
        TextField name = text(existing == null ? "" : existing.getNome(), "Nome comercial");
        TextField active = text(existing == null ? "" : existing.getPrincipioAtivo(), "Princípio ativo");
        TextField price = text(existing == null ? "" : String.valueOf(existing.getPreco()), "Ex.: 12,50");
        TextField stock = text(existing == null ? "" : String.valueOf(existing.getQuantidadeEstoque()), "Unidades");
        DatePicker expiry = date(existing == null ? null : existing.getDataValidade());
        ComboBox<String> kind = combo(List.of("Comum", "Genérico", "Controlado"));
        kind.setValue(existing == null ? "Comum" : medicineType(existing));
        List<MedicamentoComum> references = service.listar().stream()
                .filter(MedicamentoComum.class::isInstance)
                .map(MedicamentoComum.class::cast).toList();
        ComboBox<MedicamentoComum> reference = combo(references);
        reference.setConverter(converter(Medicamento::getNome, Medicamento::getId));
        if (existing instanceof MedicamentoGenerico generic) reference.setValue(generic.getMedicamentoReferencia());
        ComboBox<NivelRisco> risk = combo(NivelRisco.values());
        if (existing instanceof MedicamentoControlado controlled) risk.setValue(controlled.getNivelRisco());
        else risk.getSelectionModel().selectFirst();
        if (existing != null) {
            id.setDisable(true);
            kind.setDisable(true);
        } else {
            kind.getSelectionModel().selectFirst();
        }

        Label referenceLabel = new Label("Medicamento de referência");
        Label riskLabel = new Label("Nível de risco");
        GridPane form = formGrid();
        addField(form, 0, "ID", id);
        addField(form, 1, "Tipo", kind);
        addField(form, 2, "Nome comercial", name);
        addField(form, 3, "Princípio ativo", active);
        addField(form, 4, "Preço (R$)", price);
        addField(form, 5, "Estoque", stock);
        addField(form, 6, "Data de validade", expiry);
        form.add(referenceLabel, 0, 7);
        form.add(reference, 1, 7);
        form.add(riskLabel, 0, 8);
        form.add(risk, 1, 8);
        Runnable visibility = () -> {
            boolean generic = "Genérico".equals(kind.getValue());
            boolean controlled = "Controlado".equals(kind.getValue());
            referenceLabel.setVisible(generic);
            referenceLabel.setManaged(generic);
            reference.setVisible(generic);
            reference.setManaged(generic);
            riskLabel.setVisible(controlled);
            riskLabel.setManaged(controlled);
            risk.setVisible(controlled);
            risk.setManaged(controlled);
        };
        kind.valueProperty().addListener((obs, oldValue, newValue) -> visibility.run());
        visibility.run();
        return choose(existing == null ? "Novo medicamento" : "Editar medicamento", form, () -> {
            String medicineId = required(id, "ID");
            String medicineName = required(name, "Nome comercial");
            String principle = required(active, "Princípio ativo");
            double cost = Double.parseDouble(required(price, "Preço").replace(',', '.'));
            int amount = Integer.parseInt(required(stock, "Estoque"));
            LocalDate validUntil = required(expiry, "Data de validade");
            return switch (kind.getValue()) {
                case "Genérico" -> {
                    if (reference.getValue() == null) {
                        throw new IllegalArgumentException("Selecione o medicamento comum de referência.");
                    }
                    yield new MedicamentoGenerico(medicineId, medicineName, principle, cost, amount,
                            validUntil, reference.getValue());
                }
                case "Controlado" -> new MedicamentoControlado(medicineId, medicineName, principle,
                        cost, amount, validUntil, required(risk.getValue(), "Nível de risco"));
                default -> new MedicamentoComum(medicineId, medicineName, principle, cost, amount, validUntil);
            };
        });
    }

    private Optional<Receita> receitaForm(Optional<Receita> current,
                                         Servico<Cliente, String> clients,
                                         Servico<Medico, String> doctors) {
        Receita existing = current.orElse(null);
        TextField id = text(existing == null ? "" : String.valueOf(existing.getId()), "Número da receita");
        ComboBox<Cliente> client = combo(clients.listar());
        client.setConverter(converter(Cliente::getNome, Cliente::getId));
        ComboBox<Medico> doctor = combo(doctors.listar());
        doctor.setConverter(converter(Medico::getNome, Medico::getId));
        if (existing != null) {
            client.setValue(existing.getCliente());
            doctor.setValue(existing.getMedico());
        } else {
            if (!client.getItems().isEmpty()) client.getSelectionModel().selectFirst();
            if (!doctor.getItems().isEmpty()) doctor.getSelectionModel().selectFirst();
        }
        DatePicker issued = date(existing == null ? LocalDate.now() : existing.getDataEmissao());
        ComboBox<TipoReceita> type = combo(TipoReceita.values());
        type.setValue(existing == null ? TipoReceita.COMUM : existing.getTipo());
        TableView<PrescriptionLine> items = new TableView<>();
        items.setPrefHeight(145);
        TableColumn<PrescriptionLine, String> principleColumn = new TableColumn<>("Princípio ativo");
        principleColumn.setCellValueFactory(new PropertyValueFactory<>("principio"));
        TableColumn<PrescriptionLine, String> quantityColumn = new TableColumn<>("Qtd. prescrita");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        TableColumn<PrescriptionLine, String> remainingColumn = new TableColumn<>("Saldo");
        remainingColumn.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        items.getColumns().add(principleColumn);
        items.getColumns().add(quantityColumn);
        items.getColumns().add(remainingColumn);
        ObservableList<PrescriptionLine> lines = FXCollections.observableArrayList();
        if (existing != null) {
            existing.getItens().forEach(item -> lines.add(new PrescriptionLine(item.getPrincipioAtivo(),
                    item.getQuantidadePrescrita(), item.getQuantidadeRestante())));
        }
        items.setItems(lines);
        TextField principle = text("", "Ativo prescrito");
        TextField quantity = text("", "Quantidade");
        Button addLine = button("Adicionar item", () -> {
            try {
                lines.add(new PrescriptionLine(required(principle, "Princípio ativo"),
                        Integer.parseInt(required(quantity, "Quantidade"))));
                principle.clear();
                quantity.clear();
            } catch (RuntimeException error) {
                showError(error.getMessage());
            }
        }, "green-button");
        Button removeLine = button("Remover selecionado", () -> {
            PrescriptionLine selected = items.getSelectionModel().getSelectedItem();
            if (selected != null) lines.remove(selected);
        }, "secondary-button");
        if (existing != null) {
            id.setDisable(true);
            addLine.setDisable(true);
            removeLine.setDisable(true);
        }
        HBox itemEditor = new HBox(8, principle, quantity, addLine, removeLine);
        GridPane form = formGrid();
        addField(form, 0, "ID", id);
        addField(form, 1, "Paciente", client);
        addField(form, 2, "Médico prescritor", doctor);
        addField(form, 3, "Data de emissão", issued);
        addField(form, 4, "Tipo de receita", type);
        form.add(new Label("Itens prescritos"), 0, 5);
        form.add(items, 0, 6, 2, 1);
        form.add(itemEditor, 0, 7, 2, 1);
        return choose(existing == null ? "Nova receita" : "Editar receita", form, () -> {
            Long recipeId = Long.parseLong(required(id, "ID"));
            Cliente selectedClient = required(client.getValue(), "Paciente");
            Medico selectedDoctor = required(doctor.getValue(), "Médico");
            LocalDate issueDate = required(issued, "Data de emissão");
            TipoReceita selectedType = required(type.getValue(), "Tipo de receita");
            if (existing != null) {
                return existing.revisarCadastro(selectedClient, selectedDoctor, issueDate, selectedType);
            }
            if (lines.isEmpty()) throw new IllegalArgumentException("Adicione ao menos um item prescrito.");
            Receita recipe = new Receita(recipeId, selectedClient, selectedDoctor, issueDate, selectedType);
            lines.forEach(line -> recipe.adicionarItem(line.principio, line.quantidade));
            return recipe;
        });
    }

    private <T, ID> Adapter<T, ID> adapter(Class<T> type, String title, List<String> columns,
                                            Servico<T, ID> service, Function<T, ID> id,
                                            Function<T, List<String>> values,
                                            Function<Optional<T>, Optional<T>> editor,
                                            boolean updatable) {
        return new Adapter<>(type, title, columns, service, id, values, editor, updatable);
    }

    private <T, ID> void create(Adapter<T, ID> adapter) {
        adapter.editor.apply(Optional.empty()).ifPresent(adapter.service::cadastrar);
    }

    private <T, ID> void update(Adapter<T, ID> adapter, Object selected) {
        T current = adapter.type.cast(selected);
        adapter.editor.apply(Optional.of(current)).ifPresent(adapter.service::atualizar);
    }

    private <T, ID> void delete(Adapter<T, ID> adapter, Object selected) {
        adapter.service.excluir(adapter.id.apply(adapter.type.cast(selected)));
    }

    private Row selectedRow() {
        if (table == null || table.getSelectionModel().getSelectedItem() == null) {
            showError("Selecione um registro na tabela primeiro.");
            return null;
        }
        return table.getSelectionModel().getSelectedItem();
    }

    private Venda selectedSale() {
        if (activeSection != Section.VENDAS) {
            showError("Abra a seção Vendas para realizar esta ação.");
            return null;
        }
        Row row = selectedRow();
        if (row == null) return null;
        return (Venda) row.entity;
    }

    private void feedback(String message) {
        if (feedbackLabel != null) feedbackLabel.setText(message);
    }

    private void withErrors(Runnable operation) {
        try {
            operation.run();
        } catch (RuntimeException error) {
            showError(error.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Não foi possível concluir");
        alert.setHeaderText("Verifique os dados e as regras do sistema.");
        alert.setContentText(message == null || message.isBlank() ? "Ocorreu um erro inesperado." : message);
        own(alert);
        alert.showAndWait();
    }

    private <T> Optional<T> choose(String title, Node form, Supplier<T> submit) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        own(dialog);
        ButtonType save = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        ScrollPane formScroller = new ScrollPane(form);
        formScroller.setFitToWidth(true);
        formScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroller.setPrefViewportHeight(420);
        formScroller.setMinViewportHeight(260);
        dialog.getDialogPane().setContent(formScroller);
        dialog.getDialogPane().setPrefWidth(560);
        AtomicReference<T> result = new AtomicReference<>();
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(save);
        saveButton.getStyleClass().add("primary-button");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("secondary-button");
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                result.set(submit.get());
            } catch (RuntimeException error) {
                event.consume();
                showError(error.getMessage());
            }
        });
        dialog.setResultConverter(button -> button == save ? result.get() : null);
        return dialog.showAndWait();
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(11);
        grid.setPadding(new javafx.geometry.Insets(14));
        grid.setMinWidth(480);
        return grid;
    }

    private void addField(GridPane grid, int row, String label, Node input) {
        Label caption = new Label(label);
        caption.getStyleClass().add("form-label");
        grid.add(caption, 0, row);
        grid.add(input, 1, row);
        if (input instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(region, Priority.ALWAYS);
        }
    }

    private TextField text(String initial, String prompt) {
        TextField field = new TextField(initial);
        field.setPromptText(prompt);
        return field;
    }

    private DatePicker date(LocalDate initial) {
        DatePicker picker = new DatePicker(initial);
        picker.setPromptText("Selecione a data");
        return picker;
    }

    private <T> ComboBox<T> combo(T[] values) {
        return new ComboBox<>(FXCollections.observableArrayList(values));
    }

    private <T> ComboBox<T> combo(List<T> values) {
        return new ComboBox<>(FXCollections.observableArrayList(values));
    }

    private <T> T required(T value, String label) {
        if (value == null) throw new IllegalArgumentException("Preencha o campo " + label + ".");
        return value;
    }

    private String required(TextField field, String label) {
        String value = field.getText() == null ? "" : field.getText().trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Preencha o campo " + label + ".");
        return value;
    }

    private LocalDate required(DatePicker picker, String label) {
        if (picker.getValue() == null) throw new IllegalArgumentException("Preencha o campo " + label + ".");
        return picker.getValue();
    }

    private <T> StringConverter<T> converter(Function<T, String> display, Function<T, String> key) {
        return new StringConverter<>() {
            @Override
            public String toString(T value) {
                return value == null ? "" : display.apply(value) + " (ID: " + key.apply(value) + ")";
            }

            @Override
            public T fromString(String value) {
                return null;
            }
        };
    }

    private Button button(String text, Runnable action, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setOnAction(event -> action.run());
        return button;
    }

    private String money(Object value) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f",
                value instanceof BigDecimal decimal ? decimal.doubleValue() : ((Number) value).doubleValue());
    }

    private String medicineType(Medicamento medicamento) {
        if (medicamento instanceof MedicamentoControlado) return "Controlado";
        if (medicamento instanceof MedicamentoGenerico) return "Genérico";
        return "Comum";
    }

    private String value(String text) {
        return text == null || text.isBlank() ? "—" : text;
    }

    private <T, ID> Servico<T, ID> service(Section section) {
        @SuppressWarnings("unchecked")
        Adapter<T, ID> adapter = (Adapter<T, ID>) adapters.get(section);
        return adapter.service;
    }

    private void own(Dialog<?> dialog) {
        Window owner = contentArea.getScene() == null ? null : contentArea.getScene().getWindow();
        if (owner != null) dialog.initOwner(owner);
    }

    private static class Adapter<T, ID> {
        private final Class<T> type;
        private final String title;
        private final List<String> columns;
        private final Servico<T, ID> service;
        private final Function<T, ID> id;
        private final Function<T, List<String>> values;
        private final Function<Optional<T>, Optional<T>> editor;
        private final boolean updatable;

        private Adapter(Class<T> type, String title, List<String> columns, Servico<T, ID> service,
                        Function<T, ID> id, Function<T, List<String>> values,
                        Function<Optional<T>, Optional<T>> editor, boolean updatable) {
            this.type = type;
            this.title = title;
            this.columns = columns;
            this.service = service;
            this.id = id;
            this.values = values;
            this.editor = editor;
            this.updatable = updatable;
        }

        private List<T> list() {
            return service.listar();
        }

        private Row toRow(Object entity) {
            T item = type.cast(entity);
            return new Row(item, values.apply(item));
        }
    }

    private record Row(Object entity, List<String> values) { }

    private enum Section {
        CLIENTES("Cadastre e consulte os pacientes da farmácia.",
                List.of("ID", "Nome", "CPF", "Nascimento")),
        FARMACEUTICOS("Gerencie os profissionais responsáveis pelas dispensações.",
                List.of("ID", "Nome", "CPF", "CRF")),
        MEDICOS("Gerencie os médicos prescritores e seus registros profissionais.",
                List.of("ID", "Nome", "CPF", "CRM")),
        MEDICAMENTOS("Controle catálogo, estoque, validade e validação sanitária.",
                List.of("ID", "Nome comercial", "Princípio ativo", "Tipo", "Preço", "Estoque", "Validade", "Validação")),
        RECEITAS("Consulte prescrições, itens e validade para dispensação.",
                List.of("ID", "Paciente", "Médico", "Emissão", "Validade", "Tipo", "Itens", "Situação")),
        VENDAS("Abra vendas, adicione produtos, anexe receitas e finalize pagamentos.",
                List.of("ID", "Data", "Situação", "Cliente", "Farmacêutico", "Itens", "Receita", "Total", "Pagamento"));

        private final String description;
        private final List<String> columns;

        Section(String description, List<String> columns) {
            this.description = description;
            this.columns = columns;
        }
    }

    public static class PrescriptionLine {
        private final String principio;
        private final int quantidade;
        private final int saldo;

        public PrescriptionLine(String principio, int quantidade) {
            this(principio, quantidade, quantidade);
            if (principio.isBlank() || quantidade <= 0) {
                throw new IllegalArgumentException("Informe um princípio ativo e uma quantidade positiva.");
            }
        }

        public PrescriptionLine(String principio, int quantidade, int saldo) {
            this.principio = principio;
            this.quantidade = quantidade;
            this.saldo = saldo;
        }

        public String getPrincipio() { return principio; }
        public String getQuantidade() { return String.valueOf(quantidade); }
        public String getSaldo() { return String.valueOf(saldo); }
    }
}
