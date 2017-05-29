package com.vaadin.demo.dashboard.view.dashboard

import com.github.vok.framework.vaadin.*
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
class DashboardEdit(private val listener: DashboardEdit.DashboardEditListener, currentName: String) : Window() {

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
            // button bar
            horizontalLayout {
                addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
                w = fillParent
                button("Cancel") {
                    expandRatio = 1f
                    alignment = Alignment.TOP_RIGHT
                    setClickShortcut(KeyCode.ESCAPE)
                    onLeftClick { close() }
                }
                button("Save") {
                    setPrimary()
                    onLeftClick {
                        listener.dashboardNameEdited(nameField.value)
                        close()
                    }
                }
            }
        }
    }

    interface DashboardEditListener {
        fun dashboardNameEdited(name: String)
    }
}
