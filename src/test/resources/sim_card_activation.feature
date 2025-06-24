# src/test/resources/sim_card_activation.feature

Feature: SIM Card Activation Management

  As a Telstra SIM Card Activation System
  I want to process SIM activation requests
  So that I can activate SIMs and keep a record of the outcomes

  Scenario: Successful SIM Card Activation
    Given the SIM card activation service is running
    When an activation request is submitted for ICCID "1255789453849037777" and customer email "success@example.com"
    Then the SIM activation record with ID 1 should show ICCID "1255789453849037777" and active status true

  Scenario: Failed SIM Card Activation
    Given the SIM card activation service is running
    When an activation request is submitted for ICCID "8944500102198304826" and customer email "fail@example.com"
    Then the SIM activation record with ID 2 should show ICCID "8944500102198304826" and active status false
