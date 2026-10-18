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
import com.qualcomm.hardware.dfrobot.HuskyLens;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "first", group = "Robot")
public class TeleOp extends OpMode {
    private DcMotor topLeft, topRight, bottomLeft, bottomRight;
    private HuskyLens camera;
    private Deadline rateLimit;

    private float slowSpeed = 0.25f, regSpeed = 0.5f;

    private final int READ_PERIOD = 1;
    private HuskyLens.Block[] blocks;

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

        camera = hardwareMap.get(HuskyLens.class, "camera");
        camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
        rateLimit = new Deadline(READ_PERIOD, TimeUnit.SECONDS);
        rateLimit.expire();
    }
    @Override
    public void loop() {
        double lsY, lsX, rsX;
        float speed = regSpeed;

        if (gamepad1.right_trigger > 0.1) {
            speed = slowSpeed;
        }

        if (gamepad1.dpadDownWasPressed())
            regSpeed -= 0.05f;
        if (gamepad1.dpadUpWasPressed())
            regSpeed += 0.05f;

        lsY = -gamepad1.left_stick_y; // W_UP is now positive
        lsX = gamepad1.left_stick_x;
        rsX = gamepad1.right_stick_x; // un-bottomwards it
        topLeft.setPower((lsY + lsX + rsX)  * speed);
        topRight.setPower((lsY - lsX - rsX) * speed);
        bottomLeft.setPower((lsY - lsX + rsX)   * speed);
        bottomRight.setPower((lsY + lsX - rsX)  * speed);

        readCamera();
        for (int i = 0; i < blocks.length; i++){
            telemetry.addData("b", blocks[i].toString());
            telemetry.addData("b.y", blocks[i].y);
        }
        telemetry.addData("regSpeed", regSpeed);
        telemetry.update();
    }

    private void readCamera() {
        if (!rateLimit.hasExpired())
            return;
        rateLimit.reset();
        blocks = camera.blocks();
    }
}
