/* Copyright (c) 2025 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.LED;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "second", group = "Robot")
public class TeleOp extends OpMode {
    private DcMotor topLeft, topRight, bottomLeft, bottomRight;
    private DcMotor intake, shooter;
    private DcMotorEx theCoolerShooter;
    private CRServo leftIntake, midIntake, rightIntake;

    private LED red, green;

    private float intakeSpeed = 1.0f;

    @Override
    public void init() {
        topLeft = hardwareMap.get(DcMotor.class, "top_left");
        topRight = hardwareMap.get(DcMotor.class, "top_right");
        bottomLeft = hardwareMap.get(DcMotor.class, "bottom_left");
        bottomRight = hardwareMap.get(DcMotor.class, "bottom_right");

        bottomLeft.setDirection(DcMotor.Direction.REVERSE);
        topLeft.setDirection(DcMotor.Direction.REVERSE);

        topRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bottomRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        topLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bottomLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        theCoolerShooter = (DcMotorEx)shooter;

        leftIntake = hardwareMap.get(CRServo.class, "left_intake");
        midIntake = hardwareMap.get(CRServo.class, "mid_intake");
        rightIntake = hardwareMap.get(CRServo.class, "right_intake");

        midIntake.setDirection(DcMotor.Direction.REVERSE);
        leftIntake.setDirection(DcMotor.Direction.REVERSE);

        red = hardwareMap.get(LED.class, "red");
        green = hardwareMap.get(LED.class, "green");
    }
    @Override
    public void loop() {
        double lsY, lsX, rsX;
        float speed = 1.0f;
        float midIntakeSpeed = 0.0f;
        float shooterSpeed = 0.0f;
        boolean atSpeed = theCoolerShooter.getVelocity() >= 1540;

        if (gamepad1.right_trigger > 0.1)
                midIntakeSpeed = -1.0f;

        if (gamepad1.a)
            midIntakeSpeed = 1.0f;

        if (gamepad1.bWasPressed())
            intakeSpeed = intakeSpeed == 0.0f ? 1.0f : 0.0f;

        if (gamepad1.left_trigger > 0.1)
            shooterSpeed = 0.60f;

        green.enable(atSpeed);
        red.enable(theCoolerShooter.getVelocity() >= 1440 && !atSpeed);

        lsY = -gamepad1.left_stick_y; // W_UP is now positive
        lsX = -gamepad1.left_stick_x;
        rsX = -gamepad1.right_stick_x; // un-bottomwards it
        topLeft.setPower((lsY + lsX + rsX)  * speed);
        topRight.setPower((lsY - lsX - rsX) * speed);
        bottomLeft.setPower((lsY - lsX + rsX)   * speed);
        bottomRight.setPower((lsY + lsX - rsX)  * speed);

        leftIntake.setPower(1.0f);
        midIntake.setPower(midIntakeSpeed);
        rightIntake.setPower(1.0f);

        intake.setPower(intakeSpeed);

        shooter.setPower(shooterSpeed);

        telemetry.addData("yeah", shooterSpeed);
        telemetry.addData("nah", intakeSpeed);
        telemetry.addData("vel", theCoolerShooter.getVelocity());
        telemetry.addData(":3", gamepad1.left_trigger);
        telemetry.update();
    }
}
