package com.vaadin.demo.dashboard.view.dashboard

import com.github.vok.framework.vaadin.px
import com.github.vok.framework.vaadin.textField
import com.github.vok.framework.vaadin.verticalLayout
import com.github.vok.framework.vaadin.w
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.server.Sizeable
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme

/**
 * Simple name editor Window.
 */
class DashboardEdit(private val listener: DashboardEdit.DashboardEditListener,
                    currentName: String) : Window() {

    private lateinit var nameField: TextField

    init {
        caption = "Edit Dashboard"
        isModal = true
        isClosable = false
        isResizable = false
        w = 300.px

        addStyleName("edit-dashboard")

        verticalLayout {
            nameField = textField("Name", currentName) {
                addStyleName("caption-on-left")
                focus()
            }
            addComponent(buildFooter())
        }
    }

    private fun buildFooter(): Component {
        val footer = HorizontalLayout()
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE)

        val cancel = Button("Cancel")
        cancel.addClickListener { close() }
        cancel.setClickShortcut(KeyCode.ESCAPE)

        val save = Button("Save")
        save.addStyleName(ValoTheme.BUTTON_PRIMARY)
        save.addClickListener {
            listener.dashboardNameEdited(nameField.value)
            close()
        }
        save.setClickShortcut(KeyCode.ENTER)

        footer.addComponents(cancel, save)
        footer.setExpandRatio(cancel, 1f)
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT)
        return footer
    }

    interface DashboardEditListener {
        fun dashboardNameEdited(name: String)
    }
}
