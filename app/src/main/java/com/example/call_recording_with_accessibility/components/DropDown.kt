package com.example.call_recording_with_accessibility.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropDown(itemList: Array<CharSequence?>, onItemSelected: (Int) -> Unit) {
    // State for the expanded/collapsed state of the dropdown
    var expanded by remember { mutableStateOf(false) }

    // Selected item state
    var selectedItem by remember { mutableStateOf("Select an option") }

    // List of items to display in the dropdown
    val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")

    // Box is required for the dropdown to position itself relative to the text field
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // The text field that will show the selected item
        OutlinedTextField(
//            modifier = Modifier.clickable { expanded = true },
            value = selectedItem,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable { expanded = true },
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        // The actual dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            itemList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.toString()) },
                    onClick = {
//                        selectedItem = item
                        onItemSelected(itemList.indexOf(item))
                        expanded = false
                    }
                )
            }
        }
    }
}