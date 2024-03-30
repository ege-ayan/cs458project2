package com.example.cs458

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var defectsContainer: LinearLayout
    private lateinit var aiUseCaseEditText: EditText
    private lateinit var submitButton: Button
    private val selectedModels = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val birthDateEditText = findViewById<EditText>(R.id.birthDateEditText)
        setupBirthDatePicker(birthDateEditText)
        submitButton = findViewById(R.id.submitSurveyButton)
        submitButton.setOnClickListener {
            handleSubmitButtonClick()
        }
        setupEducationSpinner()

        defectsContainer = findViewById(R.id.defectsContainer)
        setupAIModelCheckboxes()

        aiUseCaseEditText = findViewById(R.id.aiUseCaseEditText)

        // Check fields when the activity is created
        checkFieldsForEmptyValues()

        // Set up listeners for text changes to trigger field checks
        setUpTextChangeListeners()
    }

    private fun setupBirthDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                editText.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupEducationSpinner() {
        val educationSpinner = findViewById<Spinner>(R.id.educationSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.education_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            educationSpinner.adapter = adapter
        }
    }

    private fun setupAIModelCheckboxes() {
        setupCheckBoxListener(findViewById(R.id.checkBoxChatGPT), "ChatGPT")
        setupCheckBoxListener(findViewById(R.id.checkBoxBard), "Bard")
        setupCheckBoxListener(findViewById(R.id.checkBoxClaude), "Claude")
        setupCheckBoxListener(findViewById(R.id.checkBoxCopilot), "Copilot")
    }

    private fun setupCheckBoxListener(checkBox: CheckBox, aiModel: String) {
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedModels.add(aiModel)
                val editText = EditText(this)
                editText.hint = "Defects/Cons of $aiModel"
                editText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                editText.tag = aiModel
                editText.id = when (aiModel) {
                    "ChatGPT" -> R.id.editTextChatGPT
                    "Bard" -> R.id.editTextBard
                    "Claude" -> R.id.editTextClaude
                    "Copilot" -> R.id.editTextCopilot
                    else -> View.generateViewId()
                }
                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        checkFieldsForEmptyValues()
                    }
                })
                defectsContainer.addView(editText)
            } else {
                selectedModels.remove(aiModel)
                defectsContainer.findViewWithTag<EditText>(aiModel)?.let {
                    defectsContainer.removeView(it)
                }
            }
        }
    }

    private fun handleSubmitButtonClick() {
        // Add a check here again to ensure fields are not empty (CASE 1)
        if (!checkAllFieldsNotEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText)
        val birthDateEditText = findViewById<EditText>(R.id.birthDateEditText)
        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val aiUseCaseEditText = findViewById<EditText>(R.id.aiUseCaseEditText)

        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val birthDateString = birthDateEditText.text.toString()
        val city = cityEditText.text.toString()
        val aiUseCase = aiUseCaseEditText.text.toString()

        // Validate age (CASE 2)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val birthYear = birthDateString.split("/")[2].toInt()
        val age = currentYear - birthYear
        if (age < 13 || age > 80) {
            Toast.makeText(this, "Age must be between 13 and 80 years old", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate city length (CASE 3)
        if (city.length < 2) {
            Toast.makeText(this, "City name should be at least 2 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate restricted phrases (CASE 4)
        val restrictedPhrases = arrayOf("DROP TABLE", "SQL INJECTION", "SCRIPT TAG")
        val containsRestrictedPhrase = restrictedPhrases.any { phrase ->
            name.contains(phrase, ignoreCase = true) ||
                    surname.contains(phrase, ignoreCase = true) ||
                    birthDateString.contains(phrase, ignoreCase = true) ||
                    city.contains(phrase, ignoreCase = true) ||
                    aiUseCase.contains(phrase, ignoreCase = true) ||
                    checkDefectsForRestrictedPhrase(phrase)
        }
        if (containsRestrictedPhrase) {
            Toast.makeText(this, "Please avoid using restricted phrases", Toast.LENGTH_SHORT).show()
            return
        }

        // Successful login (CASE 5)
        Toast.makeText(this, "Survey Submitted!", Toast.LENGTH_LONG).show()
    }

    private fun checkDefectsForRestrictedPhrase(phrase: String): Boolean {
        val defectsContainer = findViewById<LinearLayout>(R.id.defectsContainer)
        for (i in 0 until defectsContainer.childCount) {
            val editText = defectsContainer.getChildAt(i) as? EditText
            editText?.let {
                if (it.text.toString().contains(phrase, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }



    private fun checkFieldsForEmptyValues() {
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText)
        val birthDateEditText = findViewById<EditText>(R.id.birthDateEditText)
        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)

        val submitButton = findViewById<Button>(R.id.submitSurveyButton)

        // Check if any of the required fields is empty
        val anyFieldEmpty = nameEditText.text.isEmpty() ||
                surnameEditText.text.isEmpty() ||
                birthDateEditText.text.isEmpty() ||
                cityEditText.text.isEmpty() ||
                genderRadioGroup.checkedRadioButtonId == -1 ||
                selectedModels.size == 0 || // No AI model selected
                selectedModels.any { model ->
                    defectsContainer.findViewWithTag<EditText>(model)?.text.isNullOrEmpty()
                } ||
                aiUseCaseEditText.text.isEmpty()

        // Set submit button visibility based on field validation
        submitButton.visibility = if (anyFieldEmpty) View.INVISIBLE else View.VISIBLE
    }


    private fun setUpTextChangeListeners() {
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText)
        val birthDateEditText = findViewById<EditText>(R.id.birthDateEditText)
        val cityEditText = findViewById<EditText>(R.id.cityEditText)

        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        genderRadioGroup.setOnCheckedChangeListener { _, _ ->
            checkFieldsForEmptyValues()
        }

        val textChangeWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkFieldsForEmptyValues()
            }
        }

        nameEditText.addTextChangedListener(textChangeWatcher)
        surnameEditText.addTextChangedListener(textChangeWatcher)
        birthDateEditText.addTextChangedListener(textChangeWatcher)
        cityEditText.addTextChangedListener(textChangeWatcher)
        aiUseCaseEditText.addTextChangedListener(textChangeWatcher)
    }

    private fun checkAllFieldsNotEmpty(): Boolean {
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val surnameEditText = findViewById<EditText>(R.id.surnameEditText)
        val birthDateEditText = findViewById<EditText>(R.id.birthDateEditText)
        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)

        return nameEditText.text.isNotEmpty() &&
                surnameEditText.text.isNotEmpty() &&
                birthDateEditText.text.isNotEmpty() &&
                cityEditText.text.isNotEmpty() &&
                genderRadioGroup.checkedRadioButtonId != -1 &&
                selectedModels.size > 0 && // At least one AI model selected
                selectedModels.all { model ->
                    !defectsContainer.findViewWithTag<EditText>(model)?.text.isNullOrEmpty()
                } &&
                aiUseCaseEditText.text.isNotEmpty()
    }
}
